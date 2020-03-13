package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
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

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1

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


    def student
    def course
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

    private Tournament createTournament(String title, User user, List<Topic> topicList, Integer numOfQuestions, String startingDate, String conclusionDate){
        def tournamentDto = new TournamentDto(title, user, topicList, numOfQuestions, startingDate, conclusionDate)
        return new Tournament(tournamentDto)
    }

    def setup(){

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        student = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

        tournament1 = createTournament("T1", student, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournament2 = createTournament("T2", student, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournament3 = createTournament("T3", student, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournament4 = createTournament("T4", student, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)


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
        def result = tournamentService.ShowAllOpenTournaments()

        then: "The returned data is correct"
        result.size() == 2
        result.get(0).getId()==tournament1.getId()
        result.get(1).getId()==tournament3.getId()

    }

    def "No tournaments are open"(){
        given: "Only close tournaments"
        tournament1.setStatus(Tournament.Status.CLOSED)
        tournament2.setStatus(Tournament.Status.CLOSED)
        tournament3.setStatus(Tournament.Status.CLOSED)
        tournament4.setStatus(Tournament.Status.CLOSED)

        tournamentRepository.save(tournament1);
        tournamentRepository.save(tournament2);
        tournamentRepository.save(tournament3);
        tournamentRepository.save(tournament4);

        when:
        def result = tournamentService.ShowAllOpenTournaments()
        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "No tournaments exist"(){
        given: "No tournaments"

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "An exception is thrown"
        thrown(TutorException)

    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }


}