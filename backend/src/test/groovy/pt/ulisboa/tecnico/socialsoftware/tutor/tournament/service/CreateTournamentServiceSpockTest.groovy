package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_CREATOR_DOESNT_EXIST
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_DATES_WRONG_FORMAT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TITLE_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TOPIC_LIST_IS_EMPTY

@DataJpaTest
class CreateTournamentServiceSpockTest extends Specification{

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "TopicName"
    static final String TOPIC_NAME2 = "TopicName2"
    static final int NUMBER_OF_QUESTIONS = 1
    static final String TOURNAMENT_TITLE = "T12"

    @Autowired
    TournamentService tournamentService

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository


    def static student
    def course
    def static execution
    def topic
    def topicDto
    def static topicList
    def static startingDate
    def static conclusionDate
    def formatter

    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        execution =  new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecutionRepository.save(execution)

        student = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(student) //required to generate an id

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

    }

    def "a student creates a tournament"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        tournamentService.createTournament(execution.getId(), student.getId(), tournamentDto)

        then: "The returned data is correct"
        tournamentRepository.count() == 1L
        def result = tournamentRepository.findAll().get(0)
        result.getTitle() == TOURNAMENT_TITLE
        result.getTopicList().get(0).getName() == TOPIC_NAME
        result.getNumberOfQuestions() == NUMBER_OF_QUESTIONS
        result.getStartingDate().format(formatter) == startingDate
        result.getConclusionDate().format(formatter) == conclusionDate
    }

    @Unroll
    def "invalid arguments"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(title)
        tournamentDto.setTopicList(topiclist)
        tournamentDto.setNumberOfQuestions(nOfQuestions)
        tournamentDto.setStartingDate(startingdate)
        tournamentDto.setConclusionDate(conclusiondate)

        when:
        tournamentService.createTournament(execution.getId(), student.getId(), tournamentDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        title              | topiclist | nOfQuestions | startingdate | conclusiondate || errorMessage
        null               | topicList | 1            | startingDate | conclusionDate || TOURNAMENT_TITLE_IS_EMPTY
        "       "          | topicList | 1            | startingDate | conclusionDate || TOURNAMENT_TITLE_IS_EMPTY
        TOURNAMENT_TITLE   | null      | 1            | startingDate | conclusionDate || TOURNAMENT_TOPIC_LIST_IS_EMPTY
        TOURNAMENT_TITLE   | topicList | 0            | startingDate | conclusionDate || TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1
        TOURNAMENT_TITLE   | topicList | -1           | startingDate | conclusionDate || TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1
        TOURNAMENT_TITLE   | topicList | 1            | null         | conclusionDate || TOURNAMENT_DATES_WRONG_FORMAT
        TOURNAMENT_TITLE   | topicList | 1            | startingDate | null           || TOURNAMENT_DATES_WRONG_FORMAT

    }


    def "create tournament with conclusion date before starting date"(){
        given: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setStartingDate(conclusionDate)
        tournamentDto.setConclusionDate(startingDate)

        when:
        tournamentService.createTournament(execution.getId(), student.getId(), tournamentDto)

        then:
        thrown(TutorException)

    }

    def "create tournament with topics that do not exist"(){
        given: "a topic that is not in the database"
        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME2)
        def topicList2 = new ArrayList()
        topicList2.add(topic)
        and: "a tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setTopicList(topicList2)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        tournamentService.createTournament(execution.getId(), student.getId(), tournamentDto)

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