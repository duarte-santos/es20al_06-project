package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TournamentService{

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(int executionId, int userId, TournamentDto tournamentDto){

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        Tournament tournament = new Tournament(tournamentDto);
        tournament.setCourseExecution(courseExecution);
        tournament.setCreatingUser(user);

        checkTopics(tournament);
        checkCreatingUser(tournament);

        tournamentRepository.save(tournament);
        tournamentDto = new TournamentDto(tournament);
        return tournamentDto;
    }

    private void checkCreatingUser(Tournament tournament) {
        User user = userRepository.findByUsername(tournament.getCreatingUser().getUsername());
        if(user == null) throw new TutorException(TOURNAMENT_CREATOR_DOESNT_EXIST);
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
    public TournamentDto enrollInTournament(int studentId, Integer tournamentId){

        User user = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));
        // Testar se user pertence course blan vlaj erf

        if (tournamentId==null)
            throw new TutorException(TOURNAMENT_NOT_FOUND);

        Tournament tournament = new Tournament( // creates a new tournament from the tournamentDto
                tournamentRepository.findById(tournamentId) // finds the tournament in the db
                        .map(TournamentDto::new) // creates a tournament dto with the data from the db
                                    .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId)));

        if(tournament.getStudentList().contains(user))
            throw new TutorException(STUDENT_ALREADY_ENROLLED);

        if(tournament.getConclusionDate().isBefore(LocalDateTime.now()))
            throw new TutorException(TOURNAMENT_IS_CLOSED);

        tournament.addStudent(user);
        tournamentRepository.save(tournament);
        return new TournamentDto(tournament);

    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(Tournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }
}
