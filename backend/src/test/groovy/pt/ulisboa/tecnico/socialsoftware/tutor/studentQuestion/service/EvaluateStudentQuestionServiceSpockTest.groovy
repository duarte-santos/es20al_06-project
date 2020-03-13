package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import spock.lang.Specification;

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

    def studentQuestionService
    def questionService

    def course
    def user
    def studentQuestionDto
    def studentQuestion

    def setup() {
        //studentQuestionService = new StudentQuestionService()
        //questionService = new QuestionService()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)

        List<String> options = new ArrayList<String>()
        options.add(OPTION_CORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        options.add(OPTION_INCORRECT_CONTENT)
        studentQuestionDto = new StudentQuestionDto(1, QUESTION_TITLE, QUESTION_CONTENT, 1, options)

        studentQuestion = new StudentQuestion(course, user, studentQuestionDto)
    }

    def "teacher marks student question as approved without justification"() {
        // question is created, studentQuestion marked as approved
        when:
        def result = studentQuestionService.evaluateStudentQuestion("APPROVED", studentQuestion)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getCourse().getName() == COURSE_NAME
        result.getStudent().getId() == user.getId()
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == "APPROVED"
        result.getCorrespondingQuestion() != null
        and: "the question is created"
        questionService.findQuestions(course).size() == 1
        def question = new ArrayList<>(questionService.findQuestions(course)).get(0)
        question != null
        and: "has the correct value"
        question.getId() != null
        question.getKey() != null
        question.getTitle() == QUESTION_TITLE
        question.getContent() == QUESTION_CONTENT
        question.getImage() == null
        question.getOptions().size() == 4

        //TODO testar se corresponding question e a mm q a criada
    }

    def "teacher marks student question as approved with justification"() {
        // question is created, studentQuestion marked as approved
        when:
        def result = studentQuestionService.evaluateStudentQuestion("APPROVED", JUSTIFICATION, studentQuestion)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getCourse().getName() == COURSE_NAME
        result.getStudent().getId() == user.getId()
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == "APPROVED"
        result.getJustification() == JUSTIFICATION
        result.getCorrespondingQuestion() != null
        and: "the question is created"
        questionService.findQuestions(course).size() == 1
        def question = new ArrayList<>(questionService.findQuestions(course)).get(0)
        question != null
        and: "has the correct value"
        question.getId() != null
        question.getKey() != null
        question.getTitle() == QUESTION_TITLE
        question.getContent() == QUESTION_CONTENT
        question.getImage() == null
        question.getOptions().size() == 4
    }

    def "teacher marks student question as rejected without justification"() {
        // question is not created and exception is thrown
        when:
        studentQuestionService.evaluateStudentQuestion("REJECTED", studentQuestion)

        then: "an exception is thrown"
        thrown(TutorException)
    }

    def "teacher marks student question as rejected with justification"() {
        // question is not created and studentQuestion marked as rejected
        when:
        def result = studentQuestionService.evaluateStudentQuestion("REJECTED", JUSTIFICATION, studentQuestion)

        then: "the returned data is correct"
        result.getId() != null
        result.getKey() == 1
        result.getCourse().getName() == COURSE_NAME
        result.getStudent().getId() == user.getId()
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrectOption() == OPTION_CORRECT_CONTENT
        result.getState() == "REJECTED"
        result.getJustification() == JUSTIFICATION
        and: "the question is not created"
        result.getCorrespondingQuestion() == null
        questionService.findQuestions(course).size() == 0
    }

    def "teacher evaluates previously evaluated student question"() {
        // question is not created and exception is thrown
        given: "an evaluated question"
        studentQuestion.setState("ACCEPTED")

        when:
        studentQuestionService.evaluateStudentQuestion("REJECTED", studentQuestion)

        then: "an exception is thrown"
        thrown(TutorException)
    }

    //TODO testar imagem
}
