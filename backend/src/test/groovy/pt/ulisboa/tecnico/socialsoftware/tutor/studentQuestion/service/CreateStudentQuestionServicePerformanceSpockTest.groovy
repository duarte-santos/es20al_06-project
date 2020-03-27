package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

@DataJpaTest
class CreateStudentQuestionServicePerformanceSpockTest extends Specification {
    public static final String COURSE_NAME = "COURSE_NAME"
    public static final String FIRST_NAME = "FIRST_NAME"
    public static final String USERNAME = "USERNAME"
    public static final String QUESTION_TITLE = "QUESTION_TITLE"
    public static final String QUESTION_CONTENT = "QUESTION_CONTENT"
    public static final String OPTION_CORRECT = "OPTION_CORRECT"
    public static final String OPTION_INCORRECT = "OPTION_INCORRECT"
    public static final String ACRONYM = "C1"
    public static final String ACADEMIC_TERM = "1st Term"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    CourseRepository courseRepository

    @Autowired
    CourseExecutionRepository courseExecutionRepository

    @Autowired
    UserRepository userRepository

    def "Performance Test - Create 20000 (twenty thousand) StudentQuestion"() {
        given: "a course"
        def course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)
        and: "a course execution"
        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)
        and: "a user"
        def user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
        and: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addOption(OPTION_CORRECT)
        studentQuestionDto.addOption(OPTION_INCORRECT)
        studentQuestionDto.addOption(OPTION_INCORRECT)
        studentQuestionDto.addOption(OPTION_INCORRECT)
        studentQuestionDto.setCorrect(1)

        when:
        1.upto(2/*0000*/, { studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto) })

        then:
        true
    }

    @TestConfiguration
    static class ServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }
    }

}
