package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import java.time.LocalDateTime

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class StartTournamentServiceSpockTest extends Specification{

    static final String TOURNAMENT_TITLE = "T12"
    static final String STUDENT_NAME = "StudentName"
    static final String STUDENT_NAME2 = "StudentName2"
    static final String USERNAME = "StudentUsername"
    static final String USERNAME2 = "StudentUsername2"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1
    static final LocalDateTime EARLIER = LocalDateTime.now().minusDays(2)
    static final LocalDateTime YESTERDAY = LocalDateTime.now().minusDays(1)
    static final LocalDateTime TOMORROW = LocalDateTime.now().plusDays(1)
    static final LocalDateTime LATER = LocalDateTime.now().plusDays(2)
    public static final String QUESTION_TITLE = 'question title'

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

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    QuizAnswerRepository quizAnswerRepository


    def creator
    def static enrollingStudent
    def course
    def execution
    def topic
    def topicDto
    def static topicList
    def static topicDtoList
    def static tournament
    def tournamentDto
    def studentId
    def tournamentId
    def question
    def questionDto

    def setup(){

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        execution =  new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecutionRepository.save(execution)

        creator = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(creator)

        enrollingStudent = new User(STUDENT_NAME2, USERNAME2, 2, User.Role.STUDENT)
        userRepository.save(enrollingStudent)
        studentId = enrollingStudent.getId()

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topic = new Topic(course, topicDto)
        topicRepository.save(topic)

        topicDtoList = new ArrayList()
        topicDtoList.add(topicDto)
        topicList = new ArrayList()
        topicList.add(topic)

        def yesterday = DateHandler.toISOString(DateHandler.now().minusDays(1))
        def tomorrow = DateHandler.toISOString(DateHandler.now().plusDays(1))

        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, yesterday, tomorrow)
        tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()


        /* Create new questions associated with the created Topic */
        question = new Question()
        question.setKey(1)
        question.setCourse(course)
        question.setTitle(QUESTION_TITLE)
        question.addTopic(topic)
        question.setStatus(Question.Status.AVAILABLE)
        questionRepository.save(question)

        questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setSequence(1)

    }

    def "an enrolled student participates in the tournament"(){
        given: "An open tournament"
        tournament.setStartingDate(YESTERDAY)
        tournament.setConclusionDate(TOMORROW)
        and: "An enrolled student"
        tournamentService.enrollInTournament(enrollingStudent.getId(), tournamentId)

        when:
        tournamentService.startTournament(enrollingStudent.getId(), tournamentId)
        def result = quizAnswerRepository.findAll().get(0)
        /* The actual answering of questions is controlled by the Quiz Services, which are out of
        the scope of this test. */

        then: "The tournament was started - Quiz answers were created for the student"
        result.getQuiz() == tournament.getQuiz()
    }

    def "a student that's not enrolled tries to participate in a tournament"(){
        given: "An open tournament"
        tournament.setStartingDate(YESTERDAY)
        tournament.setConclusionDate(TOMORROW)
        tournamentService.enrollInTournament(enrollingStudent.getId(), tournamentId)
        and: "A student that's not enrolled"
        def student2 = new User("NotEnrolled", "NotEnrolled", 3, User.Role.STUDENT)
        userRepository.save(student2)

        when:
        tournamentService.startTournament(student2.getId(), tournamentId)


        then: "An exception was thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_NOT_ENROLLED
    }

    def "the student tries to participate in a tournament that has not generated its quiz yet"(){
        given: "An open tournament"
        tournament.setStartingDate(YESTERDAY)
        tournament.setConclusionDate(TOMORROW)

        when:
        tournamentService.startTournament(creator.getId(), tournamentId)

        then: "An exception was thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUIZ_CANT_BE_GENERATED
    }

    def "the student tries to participate in a tournament that hasn't started yet"(){
        given: "An open tournament"
        tournament.setStartingDate(TOMORROW)
        tournament.setConclusionDate(LATER)
        tournamentService.enrollInTournament(enrollingStudent.getId(), tournamentId)

        when:
        tournamentService.startTournament(creator.getId(), tournamentId)

        then: "An exception was thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_NOT_OPEN
    }

    def "the student tries to participate in an already finished tournament"(){
        given: "An finished tournament"
        tournament.setStartingDate(YESTERDAY)
        tournament.setConclusionDate(TOMORROW)
        tournamentService.enrollInTournament(enrollingStudent.getId(), tournamentId)
        tournament.setStartingDate(EARLIER)
        tournament.setConclusionDate(YESTERDAY)

        when:
        tournamentService.startTournament(creator.getId(), tournamentId)

        then: "An exception was thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_IS_FINISHED
    }

    def "the student tries to participate in a tournament 2 times"(){
        given: "An open tournament"
        tournament.setStartingDate(YESTERDAY)
        tournament.setConclusionDate(TOMORROW)
        and: "An enrolled student"
        tournamentService.enrollInTournament(enrollingStudent.getId(), tournamentId)

        when:
        tournamentService.startTournament(enrollingStudent.getId(), tournamentId)
        tournamentService.startTournament(enrollingStudent.getId(), tournamentId)

        then: "An exception was thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.STUDENT_ALREADY_PARTICIPATED
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }


}