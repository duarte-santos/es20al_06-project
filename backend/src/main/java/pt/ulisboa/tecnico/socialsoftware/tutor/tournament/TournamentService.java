package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.SQLException;
import java.util.ArrayList;

import static java.sql.DriverManager.println;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.DUPLICATE_TOURNAMENT;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_NOT_FOUND;

@Service
public class TournamentService{

    @Autowired
    private TournamentRepository tournamentRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(TournamentDto tournamentDto){


        Tournament tournament = new Tournament(tournamentDto);
        tournamentRepository.save(tournament);

        tournamentDto = new TournamentDto(tournament);
        return tournamentDto;
    }

    public ArrayList<TournamentDto> ShowAllOpenTournaments(){
        return null;
    }
}
