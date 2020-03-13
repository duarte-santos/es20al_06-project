package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service;

import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService;
import spock.lang.Specification;

public class EvaluateStudentQuestionServiceSpockTest extends Specification {
    def studentQuestionService

    def setup() {
        studentQuestionService = new StudentQuestionService() //TODO verificar que funciona como novo em cada teste
    }

    def "teacher marks question as approved without justification"() {
        // question is created
        expect: false
    }

    def "teacher marks question as approved with justification"() {
        // question is created
        expect: false
    }

    def "teacher marks question as rejected without justification"() {
        // question is not created
        expect: false
    }

    def "teacher marks question as rejected with justification"() {
        // question is not created
        expect: false
    }

    def "user is not a teacher"() {
        // an exception is thrown
        expect: false
    }

    def "teacher doesn't teach the studentQuestion's course"() {
        // an exception is thrown
        expect: false
    }
}
