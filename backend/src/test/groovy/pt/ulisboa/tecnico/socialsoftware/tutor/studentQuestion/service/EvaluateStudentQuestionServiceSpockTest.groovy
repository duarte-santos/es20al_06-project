package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;
import spock.lang.Specification;

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
    public static final String JUSTIFICATION = "justification"

    @Autowired
    StudentQuestionService studentQuestionService

    @Autowired
    QuestionService questionService

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
        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
        studentQuestionRepository.save(studentQuestion)
        studentQuestionDto = new StudentQuestionDto(studentQuestion)
    }

    def "teacher marks student question as approved without justification"() {
        // question is created, studentQuestion marked as approved
        when:
        def result = studentQuestionService.evaluateStudentQuestion(StudentQuestion.State.APPROVED, studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == StudentQuestion.State.APPROVED
        and: "the question is created"
        questionService.findQuestions(course.getId()).size() == 1
        def question = new ArrayList<>(questionService.findQuestions(course.getId())).get(0)
        question != null
        and: "has the correct value"
        question.getId() != null
        question.getKey() == 1
        question.getTitle() == QUESTION_TITLE
        question.getContent() == QUESTION_CONTENT
        question.getImage() == null
        question.getOptions().size() == 4
        question.getOptions().get(0).getCorrect() == true
        question.getOptions().get(1).getCorrect() == false

    }

    def "teacher marks student question as approved with justification"() {
        // question is created, studentQuestion marked as approved
        when:
        def result = studentQuestionService.evaluateStudentQuestion(StudentQuestion.State.APPROVED, JUSTIFICATION, studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == StudentQuestion.State.APPROVED
        result.getJustification() == JUSTIFICATION
        and: "the question is created"
        questionService.findQuestions(course.getId()).size() == 1
        def question = new ArrayList<>(questionService.findQuestions(course.getId())).get(0)
        question != null
        and: "has the correct value"
        question.getId() != null
        question.getKey() == 1
        question.getTitle() == QUESTION_TITLE
        question.getContent() == QUESTION_CONTENT
        question.getImage() == null
        question.getOptions().size() == 4
        question.getOptions().get(0).getCorrect() == true
    }

    def "teacher marks student question as rejected without justification"() {
        // question is not created and exception is thrown
        when:
        studentQuestionService.evaluateStudentQuestion(StudentQuestion.State.REJECTED, studentQuestionDto)

        then: "an exception is thrown"
        thrown(TutorException)
    }

    def "teacher marks student question as rejected with justification"() {
        // question is not created and studentQuestion marked as rejected
        when:
        def result = studentQuestionService.evaluateStudentQuestion(StudentQuestion.State.REJECTED, JUSTIFICATION, studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == StudentQuestion.State.REJECTED
        result.getJustification() == JUSTIFICATION
        and: "the question is not created"
        questionService.findQuestions(course.getId()).size() == 0
    }

    def "teacher evaluates previously evaluated student question"() {
        // question is not created and exception is thrown
        given: "an evaluated question"
        studentQuestion.setState(StudentQuestion.State.APPROVED)

        when:
        studentQuestionService.evaluateStudentQuestion(StudentQuestion.State.REJECTED, studentQuestionDto)

        then: "an exception is thrown"
        thrown(TutorException)
    }

    @TestConfiguration
    static class StudentQuestionServiceImplTestContextConfiguration {

        @Bean
        StudentQuestionService studentQuestionService() {
            return new StudentQuestionService()
        }

        @Bean
        QuestionService questionService() {
            return new QuestionService()
        }
    }

    //TODO testar imagem
}
