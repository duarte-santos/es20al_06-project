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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime

@DataJpaTest
class CancelTournamentServiceSpockTest extends Specification{

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
    static final String YESTERDAY = DateHandler.toISOString(DateHandler.now().minusDays(1))
    static final String TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final String LATER = DateHandler.toISOString(DateHandler.now().plusDays(2))
    public static final String QUESTION_TITLE = 'question title'

    @Autowired
    TournamentService tournamentService

    @Autowired
    QuizRepository quizRepository

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


        /* Question */
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

    def "Student cancels an existing tournament (no quiz)"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(tournamentId)
        def result = tournamentRepository.findAll()

        then: "The tournament is no longer in the data base"
        result.size() == 0

    }

    def "Student cancels a tournament with a generated quiz"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.generateTournamentQuiz(tournament, creator)
        tournamentService.cancelTournament(tournamentId)

        then: "The tournament and quiz are properly deleted"
        tournamentRepository.findAll().size() == 0
        quizRepository.findAll().size() == 0


    }


    def "Student cancels the same tournament twice"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(tournamentId)
        tournamentService.cancelTournament(tournamentId)

        then: "Throw an Exception"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_NOT_FOUND
    }

    def "Student tries to cancel the wrong tournament"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, TOMORROW, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(tournamentId + 1)

        then: "Throw an Exception"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_NOT_FOUND
    }

    def "Student cancels an open tournament"(){
        given: "a tournament"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, topicDtoList, NUMBER_OF_QUESTIONS, YESTERDAY, LATER)
        Tournament tournament = new Tournament(tournamentDto, creator)
        tournament.setCourseExecution(execution)
        tournament.setTopicList(topicList)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.cancelTournament(tournamentId)

        then: "Throw an Exception"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_IS_OPEN

    }

    def "No tournaments exist and a student tries to cancel"(){
        when:
        tournamentService.cancelTournament(1)

        then: "Throw an Exception"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.TOURNAMENT_NOT_FOUND
    }



    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }

}