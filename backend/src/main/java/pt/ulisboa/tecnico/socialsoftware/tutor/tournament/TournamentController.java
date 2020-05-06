package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.security.Principal;
import java.util.List;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;


    @PostMapping("/executions/{executionId}/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto createTournament(@PathVariable int executionId, @RequestBody TournamentDto tournamentDto) {
        formatDates(tournamentDto);
        return tournamentService.createTournament(executionId, tournamentDto);
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

    @GetMapping("/executions/{executionId}/tournaments/available")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> showAvailableTournaments(@PathVariable int executionId) {
        return tournamentService.showAvailableTournaments(executionId);
    }

    private void formatDates(TournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getStartingDate() != null && !tournament.getStartingDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setStartingDate(LocalDateTime.parse(tournament.getStartingDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getConclusionDate() !=null && !tournament.getConclusionDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setConclusionDate(LocalDateTime.parse(tournament.getConclusionDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }

}