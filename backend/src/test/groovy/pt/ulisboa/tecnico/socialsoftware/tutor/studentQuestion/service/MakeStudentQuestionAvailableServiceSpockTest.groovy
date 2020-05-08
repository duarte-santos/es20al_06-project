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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Option
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification;
import spock.lang.Unroll

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SQ_ALREADY_AVAILABLE
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.SQ_CANNOT_BECOME_QUESTION

@DataJpaTest
public class MakeStudentQuestionAvailableServiceSpockTest extends Specification {
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
    public static final String TOPIC_NAME = 'TopicOne'
    public static final String TOPIC_NAME2 = 'TopicTwo'
    public static final String JUSTIFICATION = 'Justification'


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
    TopicRepository topicRepository

    def course
    def courseExecution
    def user
    def studentQuestion
    def topic1
    def topic2

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

        topic1 = new Topic()
        topic1.setName(TOPIC_NAME)
        topic1.setCourse(course)
        topicRepository.save(topic1)
        topic2 = new Topic()
        topic2.setName(TOPIC_NAME2)
        topic2.setCourse(course)
        topicRepository.save(topic2)

        studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestion.setImage(image)
        studentQuestion.addTopics(topic1.getName())
        studentQuestion.addTopics(topic2.getName())
        studentQuestionRepository.save(studentQuestion)
    }

    @Unroll("studentQuestion made available")
    def "teacher adds approved student question to question pool"() {
        // new question is created and added to repository
        given: "an approved studentQuestion"
        studentQuestion.setState(StudentQuestion.State.APPROVED)

        when:
        studentQuestionService.makeStudentQuestionAvailable(studentQuestion.getId())

        then: "the student question is changed"
        studentQuestionRepository.count() == 1L
        def sq = studentQuestionRepository.findAll().get(0)
        sq.getState() == StudentQuestion.State.AVAILABLE
        and: "the new question is created"
        questionRepository.count() == 1L
        def q = questionRepository.findAll().get(0)
        and: "has the correct value"
        q.getId() != null
        q.getKey() != null
        q.getTitle() == QUESTION_TITLE
        q.getContent() == QUESTION_CONTENT
        checkQuestionOptions(q.getOptions())
        checkQuestionImage(q.getImage())
        checkQuestionTopics(q.getTopics())
        and: "the student question has the correct question id"
        sq.getId() == q.getId()
    }

    def checkQuestionOptions(List<Option> options) {
        return (options.size() == 4
                && options.get(0).getCorrect()
                && options.get(0).getContent() == OPTION_CORRECT_CONTENT
                && options.get(1).getContent() == OPTION_INCORRECT_CONTENT
                && options.get(2).getContent() == OPTION_INCORRECT_CONTENT
                && options.get(3).getContent() == OPTION_INCORRECT_CONTENT)
    }

    def checkQuestionImage(Image image) {
        return (image.getUrl() == URL
                && image.getWidth() == 20)
    }

    def checkQuestionTopics(Set<Topic> topics) {
        return (topics.size() == 2
                && topics.contains(topic1)
                && topics.contains(topic2)
                && topic1.getQuestions().size() == 1
                && topic2.getQuestions().size() == 1)
    }

    @Unroll("invalid arguments: #state | justification || errorMessage ")
    def "invalid input values"() {
        // question is not created and exception is thrown
        given: "a studentQuestion that is not approved"
        studentQuestion.setState(state)
        studentQuestion.setJustification(justification)

        when:
        studentQuestionService.makeStudentQuestionAvailable(studentQuestion.getId())

        then: "an exception is thrown"
        def error = thrown(TutorException)
        error.errorMessage == errorMessage

        where:
        state                                   | justification        || errorMessage
        StudentQuestion.State.AWAITING_APPROVAL | null                 || SQ_CANNOT_BECOME_QUESTION
        StudentQuestion.State.REJECTED          | JUSTIFICATION        || SQ_CANNOT_BECOME_QUESTION
        StudentQuestion.State.AVAILABLE         | null                 || SQ_ALREADY_AVAILABLE

    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

    }
}
