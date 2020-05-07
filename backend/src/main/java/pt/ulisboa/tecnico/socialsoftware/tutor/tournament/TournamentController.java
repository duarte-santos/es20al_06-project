package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.security.Principal;
import java.util.List;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;


    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(@PathVariable int executionId, Principal principal,
                                          @RequestBody TournamentDto tournamentDto) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.createTournament(executionId, user.getId(), tournamentDto);
    }

    @GetMapping("/executions/{executionId}/tournaments/show-open")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> showAllOpenTournaments(@PathVariable int executionId) {
        return tournamentService.showAllOpenTournaments(executionId);
    }

    @PutMapping("/tournaments/{tournamentId}/enroll")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentDto enrollInTournament(Principal principal, @PathVariable Integer tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.enrollInTournament(user.getId(), tournamentId);
    }

    /* Used to test with DEMO_STUDENT, since we cant have more than 1
     * This service allows a DEMO_STUDENT to enroll other students */
    @PutMapping("/tournaments/{tournamentId}/enroll/{userId}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public TournamentDto enrollInTournament(@PathVariable Integer tournamentId, @PathVariable Integer userId) {
        return tournamentService.enrollInTournament(userId, tournamentId);
    }


    @DeleteMapping("/tournaments/{tournamentId}/cancel")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'CREATOR.ACCESS')")
    public ResponseEntity cancelTournament(@PathVariable int tournamentId) {
        tournamentService.cancelTournament(tournamentId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/executions/{executionId}/tournaments/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> showAvailableTournaments(@PathVariable int executionId) {
        return tournamentService.showAvailableTournaments(executionId);
    }

    @GetMapping("/tournaments/{tournamentId}/start")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#tournamentId, 'TOURNAMENT.ACCESS')")
    public boolean startTournament(Principal principal, @PathVariable int tournamentId) {
        User user = (User) ((Authentication) principal).getPrincipal();
        return tournamentService.startTournament(user.getId(), tournamentId);
    }

}