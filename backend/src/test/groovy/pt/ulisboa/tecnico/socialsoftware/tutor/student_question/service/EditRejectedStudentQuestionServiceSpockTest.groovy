package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

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
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*

@DataJpaTest
class EditRejectedStudentQuestionServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "COURSE_NAME"
    public static final String ACRONYM = "ACRONYM"
    public static final String ACADEMIC_TERM = "ACADEMIC_TERM"
    public static final String FIRST_NAME = "FIRST_NAME"
    public static final String USERNAME = "USERNAME"

    public static final String QUESTION_TITLE = "QUESTION_TITLE"
    public static final String QUESTION_CONTENT = "QUESTION_CONTENT"
    public static final String CORRECT_OPTION = "CORRECT_OPTION"
    public static final String INCORRECT_OPTION = "INCORRECT_OPTION"
    public static final Integer CORRECT_INDEX = 1

    public static final String JUSTIFICATION = "JUSTIFICATION"
    public static final String IMAGE_URL = 'IMAGE_URL'
    public static final Integer IMAGE_WIDTH = 20

    public static final String NEW_QUESTION_TITLE = "NEW_QUESTION_TITLE"
    public static final String NEW_QUESTION_CONTENT = "NEW_QUESTION_CONTENT"
    public static final String NEW_CORRECT_OPTION = "NEW_CORRECT_OPTION"
    public static final String NEW_INCORRECT_OPTION = "NEW_INCORRECT_OPTION"
    public static final Integer NEW_CORRECT_INDEX = 2

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    def image
    def studentQuestion

    def setup() {
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        def user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)

        def options = new ArrayList<String>()
        options.add(CORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)

        image = new Image()
        image.setUrl(IMAGE_URL)
        image.setWidth(IMAGE_WIDTH)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(CORRECT_INDEX)

        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestion.setImage(image)
        studentQuestion.setJustification(JUSTIFICATION)
        studentQuestion.setState(StudentQuestion.State.REJECTED)
        studentQuestionRepository.save(studentQuestion)

    }

    def "edit a rejected student question"() {
        given: "a student question dto"
        def options = new ArrayList<String>()
        options.add(NEW_INCORRECT_OPTION)
        options.add(NEW_CORRECT_OPTION)
        options.add(NEW_INCORRECT_OPTION)
        options.add(NEW_INCORRECT_OPTION)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(NEW_QUESTION_TITLE)
        studentQuestionDto.setContent(NEW_QUESTION_CONTENT)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(NEW_CORRECT_INDEX)

        when:
        studentQuestionService.editRejectedStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "the student question is changed"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() == studentQuestion.getId()
        result.getTitle() == NEW_QUESTION_TITLE
        result.getContent() == NEW_QUESTION_CONTENT

        result.getState() == StudentQuestion.State.AWAITING_APPROVAL
        result.getJustification() == null
        // result.getImage() != null
        result.getImage().getId() == image.getId()
        result.getImage().getUrl() == IMAGE_URL
        result.getImage().getWidth() == IMAGE_WIDTH

        def resultOptions = result.getOptions()
        resultOptions.size() == 4
        result.getCorrect() == NEW_CORRECT_INDEX
        resultOptions.get(0) == NEW_INCORRECT_OPTION
        resultOptions.get(1) == NEW_CORRECT_OPTION
        resultOptions.get(2) == NEW_INCORRECT_OPTION
        resultOptions.get(3) == NEW_INCORRECT_OPTION
    }

    @Unroll("non rejected sq: #state")
    def "edit student question that wasn't rejected"() {
        given: "a non rejected student question"
        studentQuestion.setState(state)

        and: "a student question dto"
        def options = new ArrayList<String>()
        options.add(NEW_INCORRECT_OPTION)
        options.add(NEW_CORRECT_OPTION)
        options.add(NEW_INCORRECT_OPTION)
        options.add(NEW_INCORRECT_OPTION)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(NEW_QUESTION_TITLE)
        studentQuestionDto.setContent(NEW_QUESTION_CONTENT)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(NEW_CORRECT_INDEX)

        when:
        studentQuestionService.editRejectedStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == CANNOT_EDIT_SQ_REJECTED

        where:
        state << [StudentQuestion.State.AWAITING_APPROVAL, StudentQuestion.State.APPROVED, StudentQuestion.State.AVAILABLE]
    }

    def "edit rejected student question with the same data"() {
        given: "a student question dto"
        def options = new ArrayList<String>()
        options.add(CORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(CORRECT_INDEX)

        when:
        studentQuestionService.editRejectedStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == EDIT_DATA_STUDENT_QUESTION
    }

    @Unroll("invalid arguments: #title | #content | #correct_option | #incorrect_option | #correct_index || error_message")
    def "edit rejected student question with missing data"() {
        given: "a student question dto"
        def options = new ArrayList<String>()
        options.add(incorrect_option)
        options.add(correct_option)
        options.add(incorrect_option)
        options.add(incorrect_option)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(title)
        studentQuestionDto.setContent(content)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(correct_index)

        when:
        studentQuestionService.editRejectedStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == error_message

        where:
        title              | content              | correct_option     | incorrect_option      | correct_index     || error_message
        null               | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_TITLE_MISSING_DATA
        "    "             | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_TITLE_MISSING_DATA
        NEW_QUESTION_TITLE | null                 | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_CONTENT_MISSING_DATA
        NEW_QUESTION_TITLE | "    "               | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_CONTENT_MISSING_DATA
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | null               | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_OPTION_MISSING_DATA
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | "    "             | NEW_INCORRECT_OPTION  | NEW_CORRECT_INDEX || SQ_OPTION_MISSING_DATA
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | null                  | NEW_CORRECT_INDEX || SQ_OPTION_MISSING_DATA
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | "    "                | NEW_CORRECT_INDEX || SQ_OPTION_MISSING_DATA
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | 0                 || SQ_INVALID_CORRECT_OPTION
        NEW_QUESTION_TITLE | NEW_QUESTION_CONTENT | NEW_CORRECT_OPTION | NEW_INCORRECT_OPTION  | null              || SQ_INVALID_CORRECT_OPTION
    }

    @Unroll("invalid arguments: #options_count || error_message ")
    def "edit rejected student question with less or more than four options"() {
        given: "a student question dto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(NEW_QUESTION_TITLE)
        studentQuestionDto.setContent(NEW_QUESTION_CONTENT)
        addOptions(studentQuestionDto, options_count)
        studentQuestionDto.setCorrect(NEW_CORRECT_INDEX)

        when:
        studentQuestionService.editRejectedStudentQuestion(studentQuestion.getId(), studentQuestionDto)

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == error_message

        where:
        options_count || error_message
        3             || STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT
        5             || STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT
    }

    def addOptions(StudentQuestionDto studentQuestionDto, int options_count) {
        for (int i = 1; i <= options_count; i++) {
            if (i != NEW_CORRECT_INDEX)
                studentQuestionDto.addOption(NEW_INCORRECT_OPTION)
            else
                studentQuestionDto.addOption(NEW_CORRECT_OPTION)
        }
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
