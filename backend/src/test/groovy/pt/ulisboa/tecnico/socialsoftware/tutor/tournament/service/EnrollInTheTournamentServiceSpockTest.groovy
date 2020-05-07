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
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.mock.AutoAttach

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.stream.Collectors

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_DATES_WRONG_FORMAT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_DATES_WRONG_FORMAT
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_NOFQUESTIONS_SMALLER_THAN_1
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TITLE_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.TOURNAMENT_TOPIC_LIST_IS_EMPTY

@DataJpaTest
class EnrollInTheTournamentServiceSpockTest extends Specification{

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
    static final String TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final String LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))
    public static final String QUESTION_TITLE = 'question title'

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
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    QuestionRepository questionRepository;


    def creator
    def static enrollingStudent
    def course
    def execution
    def topic
    def topicDto
    def static topicList
    def static topicDtoList
    def tournamentDto
    def studentId
    def tournamentId
    def question
    def questionDto

    def setup() {

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

    def "the tournament exists, a student enrolls in it and a quiz is generated"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)
        def result = tournamentRepository.findAll().get(0)

        then: "Student is enrolled in the tournament"
        result.getStudentList().size() == 2
        result.getStudentList().get(1) == enrollingStudent

        and: "Quiz is generated"
        result.getQuiz().getTitle() == TOURNAMENT_TITLE + " (Quiz)"
        result.getQuiz().getQuizQuestions().size() == 1
    }


    def "the tournament exists and a student tries to enroll in it for the second time"(){
        given: "a tournament with a student, and that same student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        def studentList = new ArrayList()
        studentList.add(enrollingStudent)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setStudentList(studentList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    def "a student tries to enroll in a tournament after the conclusion date"(){
        given: "A tournament wth conclusion date prior to current date"
        def startingDate2 = LocalDateTime.now().minusDays(2)
        def conclusionDate2 = LocalDateTime.now().minusDays(1)
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament2 = new Tournament(tournamentDto, creator)
        tournament2.setCourseExecution(execution)
        tournament2.setStartingDate(startingDate2)
        tournament2.setConclusionDate(conclusionDate2)
        tournamentRepository.save(tournament2)
        def tournamentId2 = tournament2.getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId2)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    def "the tournament doesn't exist and a student tries to enroll in it"(){
        when:
        tournamentService.enrollInTournament(studentId, 1)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    def "the quiz cant be generated because there arent enough questions"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, 3, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)

        then: "Throw an Exception : Not enough questions"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.NOT_ENOUGH_QUESTIONS
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }

}