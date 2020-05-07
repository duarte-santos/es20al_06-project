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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.student_question.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification
import spock.lang.Unroll

@DataJpaTest
public class UpdateStudentQuestionTopicsServiceSpockTest extends Specification {
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

    @Autowired
    StudentQuestionService studentQuestionService

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
    def studentQuestionDto
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

        studentQuestion = new StudentQuestion(course, user, QUESTION_TITLE, QUESTION_CONTENT, options, 1)
        studentQuestion.setImage(image)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionDto = new StudentQuestionDto()

        topic1 = new Topic()
        topic1.setName(TOPIC_NAME)
        topic1.setCourse(course)
        topicRepository.save(topic1)

        topic2 = new Topic()
        topic2.setName(TOPIC_NAME2)
        topic2.setCourse(course)
        topicRepository.save(topic2)
    }

    def "studentQuestion topics are defined for the first time"() {
        given: "an array of new topics"
        String[] topics = [topic1.getName(), topic2.getName()]

        when:
        studentQuestionService.updateStudentQuestionTopics(studentQuestion.getId(), topics)

        then: "the studentQuestion is changed"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getTopics().size() == 2
        result.getTopics().contains(TOPIC_NAME)
        result.getTopics().contains(TOPIC_NAME2)
        and: "are not changed"
        result.getId() != null
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.correctOption() == OPTION_CORRECT_CONTENT
        result.getStudent().getId() == user.getId()
    }

    @Unroll("studentQuestion number of topics: #topicsNum ")
    def "studentQuestion topics are altered"() {
        given: "a studentQuestion with topics"
        Set<String> topics = new HashSet<String>()
        topics.add(topic1.getName())
        studentQuestion.setTopics(topics)

        and: "an array of new topics"
        String[] new_topics = createTopicsArray(topicsNum)

        when:
        studentQuestionService.updateStudentQuestionTopics(studentQuestion.getId(), new_topics)

        then: "the studentQuestion is changed"
        studentQuestionRepository.count() == 1L
        def result = studentQuestionRepository.findAll().get(0)
        result.getTopics().size() == topicsNum
        checkTopics(topicsNum, result)
        and: "are not changed"
        result.getId() != null
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.correctOption() == OPTION_CORRECT_CONTENT
        result.getStudent().getId() == user.getId()

        where:
        topicsNum << [0, 1, 2]
    }

    def createTopicsArray(int topicsNum) {
        if (topicsNum == 0)
            return []
        if (topicsNum == 1)
            return [topic2.getName()]
        if (topicsNum > 1)
            return [topic1.getName(), topic2.getName()]
    }

    def checkTopics(int topicsNum, StudentQuestion result) {
        if (topicsNum == 0)
            return true
        if (topicsNum == 1)
            return (result.getTopics().contains(TOPIC_NAME2))
        if (topicsNum == 2) {
            return (result.getTopics().contains(TOPIC_NAME) &&
                    result.getTopics().contains(TOPIC_NAME2))
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
