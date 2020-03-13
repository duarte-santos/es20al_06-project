package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TournamentRepository extends JpaRepository<Tournament, Integer> {
    @Query(value = "select * from tournaments c where c.title = :title", nativeQuery = true)
    Optional<Tournament> findByTitle(String title);

    @Query(value = "select * from tournaments t where t.status = 'OPEN'", nativeQuery = true)
    List<Tournament> findOpen();

   // @Query(value = "select studentList from tournaments t where t.status = 'OPEN'", nativeQuery = true)
    //List<User> findUsers();

}

