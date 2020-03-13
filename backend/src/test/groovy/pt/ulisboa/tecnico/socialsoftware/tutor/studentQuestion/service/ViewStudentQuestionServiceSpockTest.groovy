package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestionService
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import spock.lang.Specification

class ViewStudentQuestionServiceSpockTest extends Specification {

    def studentQuestionService

    def setup() {

    }

    def "student views empty set of studentQuestion"() {
        // throws exception
        expect: false
    }

    def "student views two studentQuestion"() {
        // validate results
        expect: false
    }

    def "student views studentQuestion with justification"() {
        // validate results
        expect: false
    }

    def "student views studentQuestion without justification"() {
        // validate results
        expect: false
    }

    def "student views studentQuestion with an image"() {
        // validate results
        expect: false
    }

}
