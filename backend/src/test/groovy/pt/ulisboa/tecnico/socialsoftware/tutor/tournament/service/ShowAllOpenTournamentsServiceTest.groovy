package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@DataJpaTest
class ShowAllOpenTournamentsServiceTest extends Specification{

    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1
    static final String TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final String LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository


    def course
    def static execution
    def topic
    def topicDto
    def static topicList
    def static startingDate
    def static conclusionDate
    def formatter
    def tournament1
    def tournament2
    def tournament3
    def tournament4

    private static Tournament createTournament(String title, List<TopicDto> topicList, Integer numOfQuestions, String startingDate, String conclusionDate){
        def tournamentDto = new TournamentDto(title, topicList, numOfQuestions, startingDate, conclusionDate)
        def tournament = new Tournament(tournamentDto)
        tournament.setCourseExecution(execution)
        return tournament
    }

    def setup(){

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        execution =  new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecutionRepository.save(execution)


        topicList = new ArrayList()

        tournament1 = createTournament("T1", topicList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        tournament2 = createTournament("T2", topicList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        tournament3 = createTournament("T3", topicList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        tournament4 = createTournament("T4", topicList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)

        tournament1.setCourseExecution(execution)
        tournament2.setCourseExecution(execution)
        tournament3.setCourseExecution(execution)
        tournament4.setCourseExecution(execution)

    }

    def "Both open and close tournaments exist"(){
        given: "several open tournaments"

        tournament1.setStatus(Tournament.Status.OPEN)
        tournament2.setStatus(Tournament.Status.CLOSED)
        tournament3.setStatus(Tournament.Status.OPEN)
        tournament4.setStatus(Tournament.Status.CLOSED)

        tournamentRepository.save(tournament1)
        tournamentRepository.save(tournament2)
        tournamentRepository.save(tournament3)
        tournamentRepository.save(tournament4)

        when:
        def result = tournamentService.showAllOpenTournaments(execution.getId())

        then: "The returned data is correct"
        result.size() == 2
        result.get(0).getId()==tournament1.getId()
        result.get(1).getId()==tournament3.getId()

    }

    def "No tournaments are open"(){
        given: "Only closed tournaments"
        tournament1.setStatus(Tournament.Status.CLOSED)
        tournament2.setStatus(Tournament.Status.CLOSED)
        tournament3.setStatus(Tournament.Status.CLOSED)
        tournament4.setStatus(Tournament.Status.CLOSED)

        tournamentRepository.save(tournament1);
        tournamentRepository.save(tournament2);
        tournamentRepository.save(tournament3);
        tournamentRepository.save(tournament4);

        when:
        def result = tournamentService.showAllOpenTournaments(execution.getId())

        then: "Empty list is returned"
        result.size() == 0
    }

    def "No tournaments exist"(){
        given: "No tournaments"

        when:
        def result = tournamentService.showAllOpenTournaments(execution.getId())

        then: "Empty list is returned"
        result.size() == 0

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }


}