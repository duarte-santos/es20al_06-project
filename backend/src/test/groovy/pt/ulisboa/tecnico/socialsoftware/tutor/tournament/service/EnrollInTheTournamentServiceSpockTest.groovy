package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
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

@DataJpaTest
class EnrollInTheTournamentServiceSpockTest extends Specification{

    static final String TOURNAMENT_TITLE = "T12"
    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String USERNAME2 = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1

    @Autowired
    TournamentService tournamentService

    @Autowired
    UserRepository userRepository

    @Autowired
    TournamentRepository tournamentRepository

    @Autowired
    TopicRepository topicRepository

    @Autowired
    CourseRepository courseRepository


    def creatingStudent
    def enrollingStudent
    def course
    def topic
    def topicDto
    def static topicList
    def static startingDate
    def static conclusionDate
    def formatter
    def tournamentDto
    def studentId
    def tournamentId

    def setup() {

        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseRepository.save(course)

        creatingStudent = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(creatingStudent)

        enrollingStudent = new User(STUDENT_NAME, USERNAME2, 2, User.Role.STUDENT)
        userRepository.save(creatingStudent)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        topicRepository.save(topic)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

    }

    def "the tournament exists and a student enrolls in it"(){
        given: "a tournament and a student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, creatingStudent, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        Tournament tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        def result = tournamentService.enrollInTournament(enrollingStudent, tournamentId)

        then: "Student is enrolled in the tournament"
        result.getStudentList().size() == 1
        result.getStudentList().get(0) == enrollingStudent
    }


    def "the tournament exists and a student tries to enroll in it for the second time"(){
        given: "a tournament with a student, and that same student"
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, creatingStudent, topicList, NUMBER_OF_QUESTIONS, startingDate, conclusionDate)
        def studentList = new ArrayList()
        studentList.add(enrollingStudent)
        tournamentDto.setStudentList(studentList)
        Tournament tournament = new Tournament(tournamentDto)
        tournamentRepository.save(tournament)
        tournamentId = tournament.getId()

        when:
        tournamentService.enrollInTournament(enrollingStudent as User, tournamentId)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    def "a student tries to enroll in a tournament after the conclusion date"(){
        given: "A tournament wth conclusion date prior to current date"
        def startingDate2 = LocalDateTime.now().minusDays(2).format(formatter)
        def conclusionDate2 = LocalDateTime.now().minusDays(1).format(formatter)
        tournamentDto = new TournamentDto(TOURNAMENT_TITLE, creatingStudent, topicList, NUMBER_OF_QUESTIONS, startingDate2, conclusionDate2)
        Tournament tournament2 = new Tournament(tournamentDto)
        tournamentRepository.save(tournament2)
        def tournamentId2 = tournament2.getId()

        when:
        tournamentService.enrollInTournament(enrollingStudent as User, tournamentId2)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    def "the tournament doesn't exist and a student tries to enroll in it"(){
        when:
        tournamentService.enrollInTournament(enrollingStudent, null)

        then: "Throw an Exception"
        thrown(TutorException)
    }

    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }

}