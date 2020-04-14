package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.*
import spock.lang.Specification

@DataJpaTest
class CreateTournamentServiceSpockPerformanceTest extends Specification {

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE = "CourseOne"
    static final String ACRONYM = "C12"
    static final String ACADEMIC_TERM = "1º Semestre"
    static final String TOPIC_NAME = "TopicName"

    @Autowired
    TournamentService tournamentService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    TopicRepository topicRepository

    def "performance testing to create 3000 tournaments"() {
        given: "a user"
        def user = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        and: "a course"
        def course = new Course(COURSE, Course.Type.TECNICO)
        courseRepository.save(course)

        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        and: "a topicList"
        def topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        def topic = new Topic(course, topicDto)
        topicRepository.save(topic)

        def topicList = new ArrayList()
        topicList.add(topicDto)
        and: "a tournamentDto"
        def tournamentDto = new TournamentDto("Torneio", topicList, 3, "2999-01-22 04:20", "2999-04-27 00:42")

        when:
        1.upto(3/*000*/, { tournamentService.createTournament(courseExecution.getId(),user.getId(),tournamentDto)})

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        TournamentService tournamentService() {
            return new TournamentService()
        }

    }
}
