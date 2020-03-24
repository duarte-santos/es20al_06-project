package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseService;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.StudentDto;

import java.util.List;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @PostMapping("/executions/{executionId}/tournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(@PathVariable int executionId, @RequestBody TournamentDto tournamentDto) {
        return tournamentService.createTournament(executionId, tournamentDto);
    }
}