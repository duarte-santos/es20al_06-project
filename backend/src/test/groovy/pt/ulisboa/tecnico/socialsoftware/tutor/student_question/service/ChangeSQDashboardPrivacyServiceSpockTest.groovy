package pt.ulisboa.tecnico.socialsoftware.tutor.student_question.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification

@DataJpaTest
class ChangeSQDashboardPrivacyServiceSpockTest extends Specification {
    public static final String COURSE_NAME = "COURSE_NAME"
    public static final String ACRONYM = "ACRONYM"
    public static final String ACADEMIC_TERM = "ACADEMIC_TERM"
    public static final String FIRST_NAME = "FIRST_NAME"
    public static final String USERNAME = "USERNAME"

    @Autowired
    UserRepository userRepository

    @Autowired
    StudentQuestionService studentQuestionService

    def user

    def setup() {
        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(user)
    }

    def "change a student's sq dashboard privacy"() {
        given:
        // nothing

        when:
        studentQuestionService.changeSQDashboardPrivacy(user.getId(), bool)

        then:
        user.getPublicSQDashboard() == bool

        where:
        bool << [false, true]
    }

    def "get the default sq dashboard privacy"() {
        given:
        // nothing

        expect:
        user.getPublicSQDashboard() == true
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }

}
