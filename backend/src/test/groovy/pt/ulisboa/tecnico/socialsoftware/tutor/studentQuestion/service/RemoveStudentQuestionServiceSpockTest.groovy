package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
public class RemoveStudentQuestionServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "CourseOne"
    public static final String ACRONYM = "C1"
    public static final String ACADEMIC_TERM = "1st Term"
    public static final String FIRST_NAME = "Student Name"
    public static final String USERNAME = "Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CORRECT_CONTENT = "Correct Content"
    public static final String OPTION_INCORRECT_CONTENT = "Incorrect Content"
    public static final String URL = 'URL'
    public static final String JUSTIFICATION = "justification"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionRepository questionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    ImageRepository imageRepository

    @Autowired
    QuizQuestionRepository quizQuestionRepository

    @Autowired
    QuizRepository quizRepository

    def course
    def courseExecution
    def user
    def studentQuestionDto
    def studentQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)

        studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestion.setImage(image)
        studentQuestionRepository.save(studentQuestion)
    }

    @Unroll("studentQuestion evaluation: #evaluation | #justification")
    def "remove question with different states"() {
        given: "an evaluation"
        studentQuestion.setState(evaluation)
        studentQuestion.setJustification(justification)
        if (available) //test when there is a corresponding question
            studentQuestionService.makeStudentQuestionAvailable(studentQuestion.getId())

        when:
        studentQuestionService.removeStudentQuestion(studentQuestion.getId())

        then: "the studentQuestion is removed"
        studentQuestionRepository.count() == 0L
        imageRepository.count() == 0L

        and: "if created, the new question has been removed"
        questionRepository.count() == 0L

        where:
        evaluation                                 | justification      | available
        StudentQuestion.State.AWAITING_APPROVAL    | null               | false
        StudentQuestion.State.REJECTED             | JUSTIFICATION      | false
        StudentQuestion.State.APPROVED             | JUSTIFICATION      | false
        StudentQuestion.State.APPROVED             | JUSTIFICATION      | true
    }

    def "invalid input values"() {
        given: "an approved student question"
        studentQuestion.setState(StudentQuestion.State.APPROVED)
        studentQuestionService.makeStudentQuestionAvailable(studentQuestion.getId())
        and: "a corresponding question with answers"
        def question = questionRepository.findAll().get(0)
        def quiz = new Quiz()
        quiz.setKey(1)
        quizRepository.save(quiz)
        def quizQuestion = new QuizQuestion()
        quizQuestionRepository.save(quizQuestion)
        question.addQuizQuestion(quizQuestion)
        quizQuestion.setQuiz(quiz)
        quiz.addQuizQuestion(quizQuestion)

        when:
        studentQuestionService.removeStudentQuestion(studentQuestion.getId())

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == ErrorMessage.QUESTION_IS_USED_IN_QUIZ
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
