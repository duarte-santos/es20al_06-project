package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class CreateStudentQuestionServiceSpockTest extends Specification {
    static final String COURSE_NAME = "CourseOne"
    static final String ACRONYM = "C1"
    static final String ACADEMIC_TERM = "1st Term"
    static final String FIRST_NAME = "Student Name"
    static final String USERNAME = "Username"
    static final String QUESTION_TITLE = "Question Title"
    static final String QUESTION_CONTENT = "Question Content"
    static final String OPTION_CORRECT_CONTENT = "Correct Content"
    static final String OPTION_INCORRECT_CONTENT = "Incorrect Content"

    def studentQuestionService

    def course
    def courseExecution
    def user

    def setup() {
        studentQuestionService = new StudentQuestionService() //TODO verificar que funciona como novo em cada teste

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        user = new User(FIRST_NAME, USERNAME, 1, User.Role.STUDENT)
    }

    def "create question with no image and four options"() {
        // the student question is created
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        def result = studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getCourse().getName() == COURSE_NAME
        result.getStudent().getId() == user.getId()
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getImage() == null
        result.getOptions().size() == 4
        result.getCorrect() == OPTION_CORRECT_CONTENT
        and: "the student question is created"
        studentQuestionService.getStudentQuestions().size() == 1
        def studentQuestion = new ArrayList<>(studentQuestionService.getStudentQuestions()).get(0)
        studentQuestion != null
        and: "has the correct value"
        studentQuestion.getId() != null
        studentQuestion.getCourse().getName() == COURSE_NAME
        studentQuestion.getStudent().getId() == user.getId()
        studentQuestion.getTitle() == QUESTION_TITLE
        studentQuestion.getContent() == QUESTION_CONTENT
        studentQuestion.getImage() == null
        studentQuestion.getOptions().size() == 4
        studentQuestion.getCorrect() == OPTION_CORRECT_CONTENT
    }

    def "create question with an image and four options"() {
        // the student question is created
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        and: "an image"
        def image = new ImageDto()
        image.setUrl(URL)
        image.setWidth(20)
        studentQuestionDto.setImage(image)

        when:
        def result = studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then: "the returned data is correct"
        result.getId() != null
        result.getCourse().getName() == COURSE_NAME
        result.getStudent().getId() == user.getId()
        result.getTitle() == QUESTION_TITLE
        result.getContent() == QUESTION_CONTENT
        result.getOptions().size() == 4
        result.getCorrect() == OPTION_CORRECT_CONTENT
        result.getImage().getId() != null
        result.getImage().getUrl() == URL
        result.getImage().getWidth() == 20
        and: "the student question is created"
        studentQuestionService.getStudentQuestions().size() == 1
        def studentQuestion = new ArrayList<>(course.getStudentQuestions()).get(0)
        studentQuestion != null
        and: "has the correct value"
        studentQuestion.getId() != null
        studentQuestion.getCourse().getName() == COURSE_NAME
        studentQuestion.getStudent().getId() == user.getId()
        studentQuestion.getTitle() == QUESTION_TITLE
        studentQuestion.getContent() == QUESTION_CONTENT
        studentQuestion.getOptions().size() == 4
        studentQuestion.getCorrect() == OPTION_CORRECT_CONTENT
        studentQuestion.getImage().getId() != null
        studentQuestion.getImage().getUrl() == URL
        studentQuestion.getImage().getWidth() == 20

    }

    def "the question title is empty"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question title is blank"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle("    ")
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question content is empty"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "the question content is blank"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent("    ")
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with less than four options"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with no correct answer"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    def "create question with more than four options"() {
        // an exception is thrown
        given: "a studentQuestionDto"
        def studentQuestionDto = new StudentQuestionDto()
        studentQuestionDto.setTitle(QUESTION_TITLE)
        studentQuestionDto.setContent(QUESTION_CONTENT)
        studentQuestionDto.setCorrect(OPTION_CORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)
        studentQuestionDto.addIncorrect(OPTION_INCORRECT_CONTENT)

        when:
        studentQuestionService.createStudentQuestion(course.getId(), user.getId(), studentQuestionDto)

        then:
        thrown(TutorException)
    }

    //TODO verificar blank e empty opcoes?

}
