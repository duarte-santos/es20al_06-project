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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification


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

    def "create question with no image and four options"() {
        // the student question is created
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        def result = studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        and: "the student question is created"
        studentQuestionService.findStudentQuestions(course.getId()).size() == 1
        def studentQuestion = new ArrayList<>(studentQuestionService.findStudentQuestions(course.getId())).get(0)
        studentQuestion != null
        and: "has the correct value"
        studentQuestion.getId() != null
        studentQuestion.getKey() == 1
        studentQuestion.getTitle() == QUESTION_TITLE
        studentQuestion.getContent() == QUESTION_CONTENT
        studentQuestion.getImage() == null
        studentQuestion.getOptions().size() == 4
        studentQuestion.getCorrectOption() == OPTION_CORRECT_CONTENT
    }

    def "create question with an image and four options"() {
        // the student question is created
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)
        and: "an image"
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        studentQuestionDto.setImage(image)

        when:
        def result = studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == URL
        result.getImage().getWidth() == 20
        and: "the student question is created"
        studentQuestionService.findStudentQuestions(course.getId()).size() == 1
        def studentQuestion = new ArrayList<>(studentQuestionService.findStudentQuestions(course.getId())).get(0)
        studentQuestion != null
        and: "has the correct value"
        studentQuestion.getId() != null
        studentQuestion.getKey() == 1
        studentQuestion.getTitle() == QUESTION_TITLE
        studentQuestion.getContent() == QUESTION_CONTENT
        studentQuestion.getOptions().size() == 4
        studentQuestion.getCorrectOption() == OPTION_CORRECT_CONTENT
        studentQuestion.getImage().getId() != null
        studentQuestion.getImage().getUrl() == URL
        studentQuestion.getImage().getWidth() == 20

    }

    def "the question title is empty"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question title is blank"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle("    ")
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question content is empty"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question content is blank"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent("    ")
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with less than four options"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with no correct answer"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with invalid correct answer"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.setCorrect(0)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with more than four options"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setKey(1)
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addOption(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.setCorrect(1)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    /*
   @Unroll("invalid arguments: #key | #title | #content | #correctContent | #incorrectContent | #correctSequence")
   def "invalid input values"() {
       given: "a studentQuestionDto"
       def studentQuestionDto = new StudentQuestionDto()
       studentQuestionDto.setKey(key)
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
       key     | title             | content           | correctContent            | incorrectContent          | correctSequence   || errorMessage
       null    | QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | null              | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | "    "            | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | QUESTION_TITLE    | null              | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | QUESTION_TITLE    | "    "            | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | QUESTION_TITLE    | QUESTION_CONTENT  | null                      | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | QUESTION_TITLE    | QUESTION_CONTENT  | "    "                    | OPTION_INCORRECT_CONTENT  | 1                 ||
       1       | QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | null                      | 1                 ||
       1       | QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | "    "                    | 1                 ||
       1       | QUESTION_TITLE    | QUESTION_CONTENT  | OPTION_CORRECT_CONTENT    | OPTION_INCORRECT_CONTENT  | null              ||
   }
   */

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

    //TODO addtopics!
    //TODO verificar blank e empty opcoes?

    //TODO key question = key student question??
    //private String justification; em StudentQuestion // TODO approval/rejection class?



}
