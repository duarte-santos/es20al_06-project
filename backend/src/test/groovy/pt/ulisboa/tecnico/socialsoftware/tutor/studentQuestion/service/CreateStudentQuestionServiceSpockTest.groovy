package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import spock.lang.Specification

class CreateStudentQuestionServiceSpockTest extends Specification {

    def sQuestionService

    def setup() {
        sQuestionService = new StudentQuestionService()
    }

    def "create question with no image and 4 options"() {
        // the student question is created
        expect: false
    }

    def "create question with an image and 4 options"() {
        // the student question is created
        expect: false
    }

    def "the course does not exist"() {
        // an exception is thrown
        expect: false
    }

    def "at least one topic does not exist"() {
        // an exception is thrown
        expect: false
    }

    def "the question title is empty"() {
        // an exception is thrown
        expect: false
    }

    def "the question title is blank"() {
        // an exception is thrown
        expect: false
    }

    def "the question content is empty"() {
        // an exception is thrown
        expect: false
    }

    def "the question content is blank"() {
        // an exception is thrown
        expect: false
    }

    def "create question with no options"() {
        // an exception is thrown
        expect: false
    }

    def "create question with no correct answer"() {
        // an exception is thrown
        expect: false
    }

    def "create question with more than 1 correct answer"() {
        // an exception is thrown
        expect: false
    }

}
