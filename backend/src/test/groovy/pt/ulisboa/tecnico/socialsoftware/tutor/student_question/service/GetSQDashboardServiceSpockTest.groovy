package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification

@DataJpaTest
class GetSQDashboardServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "COURSE_NAME"
    public static final String ACRONYM = "ACRONYM"
    public static final String ACADEMIC_TERM = "ACADEMIC_TERM"
    public static final String FIRST_NAME = "FIRST_NAME"
    public static final String USERNAME = "USERNAME"

    public static final String QUESTION_TITLE_1 = "QUESTION_TITLE"
    public static final String QUESTION_CONTENT = "QUESTION_CONTENT"
    public static final String CORRECT_OPTION = "CORRECT_OPTION"
    public static final String INCORRECT_OPTION = "INCORRECT_OPTION"
    public static final Integer CORRECT_INDEX = 1
    public static final String JUSTIFICATION = "JUSTIFICATION"

    public static final String QUESTION_TITLE_2 = "QUESTION_TITLE_2"
    public static final String QUESTION_TITLE_3 = "QUESTION_TITLE_3"
    public static final String QUESTION_TITLE_4 = "QUESTION_TITLE_4"

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

    def user
    def course

    def setup() {
        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        def courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseExecutionRepository.save(courseExecution)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
    }

    def "get a student question dashboard"() {
        given: "four student questions in which two are approved"
        def options = new ArrayList<String>()
        options.add(CORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)
        options.add(INCORRECT_OPTION)

        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE_1)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setOptions(options)
        studentQuestionDto.setCorrect(CORRECT_INDEX)

        def rejectedSQ = new StudentQuestion(course, user, studentQuestionDto)
        rejectedSQ.setJustification(JUSTIFICATION)
        rejectedSQ.setState(StudentQuestion.State.REJECTED)
        studentQuestionRepository.save(rejectedSQ)

        studentQuestionDto.setTitle(QUESTION_TITLE_2)
        def approvedSQ = new StudentQuestion(course, user, studentQuestionDto)
        approvedSQ.setJustification(JUSTIFICATION)
        approvedSQ.setState(StudentQuestion.State.APPROVED)
        studentQuestionRepository.save(approvedSQ)

        studentQuestionDto.setTitle(QUESTION_TITLE_3)
        def availableSQ = new StudentQuestion(course, user, studentQuestionDto)
        availableSQ.setState(StudentQuestion.State.AVAILABLE)
        studentQuestionRepository.save(availableSQ)

        studentQuestionDto.setTitle(QUESTION_TITLE_4)
        def awaitingSQ = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(awaitingSQ)

        when:
        def result = studentQuestionService.getSQDashboard(user.getId())

        then:
        result.getTotalQuestions() == 4
        result.getTotalApprovedQuestions() == 2
    }

    def "get an empty student question dashboard"() {
        given:
        // nothing

        when:
        def result = studentQuestionService.getSQDashboard(user.getId())

        then:
        result.getTotalQuestions() == 0
        result.getTotalApprovedQuestions() == 0
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
