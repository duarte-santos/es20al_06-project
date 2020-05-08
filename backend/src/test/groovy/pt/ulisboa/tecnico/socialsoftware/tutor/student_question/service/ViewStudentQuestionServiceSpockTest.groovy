package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class ViewStudentQuestionServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "CourseOne"
    public static final String ACRONYM = "C1"
    public static final String ACADEMIC_TERM = "1st Term"
    public static final String FIRST_NAME = "Student Name"
    public static final String USERNAME = "Username"
    public static final String QUESTION_TITLE = "Question Title"
    public static final String QUESTION_CONTENT = "Question Content"
    public static final String OPTION_CORRECT_CONTENT = "Correct Content"
    public static final String OPTION_INCORRECT_CONTENT = "Incorrect Content"
    public static final String JUSTIFICATION = "justification"
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
    def studentQuestion

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
    }


    def "student views empty set of studentQuestion"() {
        when: // result contains a list of existing studentQuestions of a particular student
        studentQuestionService.findStudentQuestions(course.getId())

        then: "the result is empty"
        studentQuestionRepository.count() == 0
    }


    def "student views two studentQuestion in which one has an image and a justification"() {
        given: "an image"
        def image = new Image()
        image.setUrl(URL)
        image.setWidth(20)

        and: "two studentQuestion"
        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)

        studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestion.setImage(image)
        studentQuestion.setState(StudentQuestion.State.REJECTED)
        studentQuestion.setJustification(JUSTIFICATION)
        studentQuestionRepository.save(studentQuestion)

        studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestionRepository.save(studentQuestion)

        when: // result contains a list of existing studentQuestions of a particular student
        studentQuestionService.viewOwnStudentQuestions(user.getId())

        then: "the returned data is correct"
        studentQuestionRepository.count() == 2L
        def result = studentQuestionRepository.findAll()

        def res0 = result.get(0)
        res0.getId() != null
        res0.getTitle() == QUESTION_TITLE
        res0.getContent() == QUESTION_CONTENT
        res0.getCorrect() == 1
        res0.getOptions() == options
        res0.getImage().getUrl() == URL
        res0.getImage().getWidth() == 20
        res0.getJustification() == JUSTIFICATION
        res0.getState() == StudentQuestion.State.REJECTED

        def res1 = result.get(1)
        res1.getId() != null
        res1.getTitle() == QUESTION_TITLE
        res1.getContent() == QUESTION_CONTENT
        res1.getCorrect() == 1
        res1.getOptions() == options
        res1.getImage() == null
        res1.getJustification() == null
        res1.getState() == StudentQuestion.State.AWAITING_APPROVAL

    }


    @TestConfiguration
    static class StudentViewServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}
