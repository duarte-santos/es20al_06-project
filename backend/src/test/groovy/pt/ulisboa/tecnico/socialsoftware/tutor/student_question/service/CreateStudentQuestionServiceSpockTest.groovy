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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;



@DataJpaTest
class CreateStudentQuestionServiceSpockTest extends Specification {
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


    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    StudentQuestionRepository studentQuestionRepository

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def course
    def courseExecution
    def user

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
    }

    @Unroll("studentQuestion creation: image #hasImage ")
    def "create question with or without image"() {
        // the student question is created
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)
        and: "an image"
        createImage(hasImage, studentQuestionDto);

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then: "the correct studentQuestion is inside the repository"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getId() != null
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.correctOption() == OPTION_CORRECT_CONTENT
        checkImage(hasImage, result)
        result.getStudent().getId() == user.getId()

        where:
        hasImage << [false, true]
    }

    def createImage(boolean hasImage, StudentQuestionDto studentQuestionDto) {
        if (hasImage) {
            def image = new ImageDto()
            image.setUrl(URL)
            image.setWidth(20)
            studentQuestionDto.setImage(image)
        }
    }

    def checkImage(boolean hasImage, StudentQuestion result) {
        if (!hasImage)
            return result.getImage() == null
        if (hasImage) {
            return (result.getImage().getId() != null &&
                    result.getImage().getUrl() == URL &&
                    result.getImage().getWidth() == 20)
        }
    }

    @Unroll("invalid arguments: #numOptions || errorMessage ")
    def "create question with less or more than four options"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        createIncorrectOptions(numOptions, studentQuestionDto)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        numOptions  || errorMessage
        1           || STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT
        4           || STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT
    }

    def createIncorrectOptions(int numOptions, StudentQuestionDto studentQuestionDto) {
        for (int i = 0; i < numOptions; i++)
            studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
    }

   @Unroll("invalid arguments: #title | #content | #correctContent | #incorrectContent | #correctSequence || errorMessage ")
   def "invalid input values"() {
       given: "a studentQuestionDto"
       def studentQuestionDto = new StudentQuestionDto()
       studentQuestionDto.setTitle(title)
       studentQuestionDto.setContent(content)
       studentQuestionDto.addOption(correctContent)
       studentQuestionDto.addOption(incorrectContent)
       studentQuestionDto.addOption(incorrectContent)
       studentQuestionDto.addOption(incorrectContent)
       studentQuestionDto.setCorrect(correctSequence)


       when:
       studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

       then: "an exception is thrown"
       def error = thrown(TutorException)
       error.errorMessage == errorMessage

       where:
       title             | content           | correctContent            | incorrectContent          | correctSequence   || errorMessage
       null              | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 || SQ_TITLE_MISSING_DATA
       "    "            | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 || SQ_TITLE_MISSING_DATA
       QUESTION_TITLE    | null              | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 || SQ_CONTENT_MISSING_DATA
       QUESTION_TITLE    | "    "            | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 || SQ_CONTENT_MISSING_DATA
       QUESTION_TITLE    | QUESTION_CONTENT  | null                      | OPTION_INCORRECT_CONTENT  | 1                 || SQ_OPTION_MISSING_DATA
       QUESTION_TITLE    | QUESTION_CONTENT  | "    "                    | OPTION_INCORRECT_CONTENT  | 1                 || SQ_OPTION_MISSING_DATA
       QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | null                      | 1                 || SQ_OPTION_MISSING_DATA
       QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | "    "                    | 1                 || SQ_OPTION_MISSING_DATA
       QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 0                 || SQ_INVALID_CORRECT_OPTION
       QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | null              || SQ_INVALID_CORRECT_OPTION
   }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}
