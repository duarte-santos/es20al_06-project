package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.JUSTIFICATION_MISSING_DATA
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.QUESTION_ALREADY_EVALUATED

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
    UserRepository userRepository

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def course
    def user
    def studentQuestionDto
    def studentQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        studentQuestionDto = new StudentQuestionDto(1, QUESTION_TITLE, QUESTION_CONTENT, 1, options)
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        studentQuestionDto.setImage(image)
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
    }

    @Unroll("studentQuestion evaluation: #evaluation | #justification || questionCreated ")
    def "teacher evaluates question with or without justification"() {
        // question is created, studentQuestion marked as approved
        when:
        def result = studentQuestionService.evaluateStudentQuestion(evaluation, justification, studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == evaluation
        and: "the new question is or is not created"
        questionWasCreated(questionCreated)
        and: "if created, has the correct value"
        checkNewQuestion(questionCreated)

        where:
        evaluation                           | justification      || questionCreated
        StudentQuestion.State.APPROVED       | null               || true
        StudentQuestion.State.APPROVED       | JUSTIFICATION      || true
        StudentQuestion.State.REJECTED       | JUSTIFICATION      || false
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
                    && question.getKey() == 1
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
        given: "a studentQuestion"
        studentQuestion.setState(previousState)

        when:
        studentQuestionService.evaluateStudentQuestion(newState, justification, studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        previousState                           | newState                        | justification      || errorMessage
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.APPROVED  | '  '               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.REJECTED  | '  '               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.AWAITING_APPROVAL | StudentQuestion.State.REJECTED  | null               || JUSTIFICATION_MISSING_DATA
        StudentQuestion.State.APPROVED          | StudentQuestion.State.APPROVED  | null               || QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.APPROVED          | StudentQuestion.State.REJECTED  | JUSTIFICATION      || QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.REJECTED          | StudentQuestion.State.APPROVED  | null               || QUESTION_ALREADY_EVALUATED
        StudentQuestion.State.REJECTED          | StudentQuestion.State.REJECTED  | JUSTIFICATION      || QUESTION_ALREADY_EVALUATED

    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
