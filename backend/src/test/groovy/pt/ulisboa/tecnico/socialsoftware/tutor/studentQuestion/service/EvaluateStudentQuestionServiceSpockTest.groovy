package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
public class EvaluateStudentQuestionServiceSpockTest extends Specification {
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

        studentQuestion = new StudentQuestion(course, user, 1, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestion.setImage(image)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
    }

    @Unroll("studentQuestion evaluation: #evaluation | #justification || questionCreated ")
    def "teacher evaluates question with or without justification"() {
        // question is created, studentQuestion marked as approved
        given: "an evaluation"
        studentQuestionDto.setState(evaluation.name())
        studentQuestionDto.setJustification(justification)

        when:
        studentQuestionService.evaluateStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "the studentQuestion is changed"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getState() == evaluation
        result.getJustification() == justification
        and: "are not changed"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.correctOption() == OPTION_CORRECT_CONTENT
        result.getStudent().getId() == user.getId()
        and: "the new question is or is not created"
        questionWasCreated(questionCreated)
        and: "if created, has the correct value"
        checkNewQuestion(questionCreated)

        where:
        evaluation                        | justification      || questionCreated
        StudentQuestion.State.APPROVED    | null               || true
        StudentQuestion.State.APPROVED    | JUSTIFICATION      || true
        StudentQuestion.State.REJECTED    | JUSTIFICATION      || false
    }

    def questionWasCreated(boolean questionCreated) {
        if (!questionCreated)
            return questionRepository.count() == 0L
        if (questionCreated)
            return questionRepository.count() == 1L
    }

    def checkNewQuestion(boolean questionCreated) {
        if (questionCreated) {
            def question = questionRepository.findAll().get(0)
            return (question.getId() != null
                    && question.getKey() != null
                    && question.getTitle() == QUESTION_TITLE
                    && question.getContent() == QUESTION_CONTENT
                    && question.getOptions().size() == 4
                    && question.getOptions().get(0).getCorrect()
                    && !question.getOptions().get(1).getCorrect()
                    && question.getImage().getId() != null
                    && question.getImage().getUrl() == URL
                    && question.getImage().getWidth() == 20)
        }
        return true;
    }

    @Unroll("invalid arguments: #previousState | #newState | #justification || errorMessage ")
    def "invalid input values"() {
        // question is not created and exception is thrown
        given: "a studentQuestion with a given state"
        studentQuestion.setState(previousState)
        and: "an evaluation"
        studentQuestionDto.setState(newState.name())
        studentQuestionDto.setJustification(justification)

        when:
        studentQuestionService.evaluateStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        previousState                           | newState                        | justification      || errorMessage
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.APPROVED  | '  '               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.REJECTED  | '  '               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.REJECTED  | null               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.APPROVED          | StudentQuestion.State.APPROVED  | null               || STUDENT_QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.APPROVED          | StudentQuestion.State.REJECTED  | JUSTIFICATION      || STUDENT_QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.REJECTED          | StudentQuestion.State.APPROVED  | null               || STUDENT_QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.REJECTED          | StudentQuestion.State.REJECTED  | JUSTIFICATION      || STUDENT_QUESTION_ALREADY_EVALUATED

    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
