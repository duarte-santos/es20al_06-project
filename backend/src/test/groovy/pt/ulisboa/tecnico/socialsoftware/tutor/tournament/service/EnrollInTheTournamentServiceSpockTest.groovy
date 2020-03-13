package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class EnrollInTheTournamentServiceSpockTest extends Specification{

    static final String TOURNAMENT_TITLE = "T12"
    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1st Semester"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1


    def student
    def course
    def topic
    def topicDto
    def static topicList
    def static startingDate
    def static conclusionDate
    def formatter
    def tournamentDto
    def studentId
    def tournamentService
    def tournamentId

    def setup() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        tournamentService = new TournamentService()

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        //courseRepository.save(course)

        student = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        //userRepository.save(student) //required to generate an id
        studentId = student.getId()

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        //topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

    }

    def "the tournament exists and a student enrolls in it"(){
        given: "a tournament and a student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, studentId, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournamentId = tournamentDto.getId()
        when:
        def result = tournamentService.enrollInTournament(studentId, tournamentId)
        then: "Student is enrolled in the tournament"

        result.getStudentList().get(0) == studentId
    }


    def "the tournament exists and a student tries to enroll in it for the second time"(){
        given: "a tournament with a student, and that same student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, studentId, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournamentId = tournamentDto.getId()
        def studentList = new ArrayList()
        studentList.add(studentId)
        tournamentDto.setStudentList(studentList)

        when:
        tournamentService.enrollInTournament(studentId, tournamentId)

        then: "Exception"
        thrown(TutorException)
    }

    def "the tournament doesn't exist and a student tries to enroll in it"(){
        given: "a non-saved tournament and a student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, studentId, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        tournamentId = tournamentDto.getId()

        when:
        // Needs DB implementation
        tournamentService.enrollInTournament(studentId, null)

        then: "Exception"
        thrown(TutorException)
    }

}