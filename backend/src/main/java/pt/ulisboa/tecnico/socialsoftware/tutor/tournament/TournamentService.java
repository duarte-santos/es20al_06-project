package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TournamentService{

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TopicRepository topicRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto tournamentDto){

        Tournament tournament = new Tournament(tournamentDto);

        checkTopics(tournament);

        tournamentRepository.save(tournament);

        tournamentDto = new TournamentDto(tournament);
        return tournamentDto;
    }

    private void checkTopics(Tournament tournament) {
        for (Topic topic : tournament.getTopicList()) {
            Topic topic2 = topicRepository.findTopicByName(topic.getCourse().getId(), topic.getName());
            if (topic2 == null) throw new TutorException(TOURNAMENT_TOPIC_DOESNT_EXIST, topic.getId());
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> ShowAllOpenTournaments(){

            List<TournamentDto> TournamentsList = tournamentRepository.findOpen().stream().map(TournamentDto::new).collect(Collectors.toList());
            if (TournamentsList.isEmpty())
                throw new TutorException(NO_OPEN_TOURNAMENTS);
            else
                return TournamentsList;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInTournament(User student, Integer tournamentId){

        if (tournamentId==null)
            throw new TutorException(TOURNAMENT_DOESNT_EXIST);

        Tournament tournament = new Tournament( // creates a new tournament from the tournamentDto
                tournamentRepository.findById(tournamentId) // finds the tournament in the db
                        .map(TournamentDto::new) // creates a tournament dto with the data from the db
                                    .orElseThrow(() -> new TutorException(TOURNAMENT_DOESNT_EXIST, tournamentId)));

        if(tournament.isEnrolled(student))
            throw new TutorException(TOURNAMENT_USER_ENROLLED_ALREADY);

        tournament.enroll(student);
        tournamentRepository.save(tournament);
        return new TournamentDto(tournament);

    }
}
