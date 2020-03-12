package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.TopicService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.TournamentService
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserService;
import spock.lang.Specification

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACADEMIC_TERM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACRONYM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_EXECUTION_ACRONYM_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_NAME_IS_EMPTY
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.COURSE_TYPE_NOT_DEFINED


class ShowAllOpenTournamentsServiceTest extends Specification{

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1st Semester"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1


    def tournamentService
    def student
    def course
    def courseExecution
    def topic
    def topicDto
    def topicList
    def startingDate
    def conclusionDate
    def formatter

    def setup(){
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        courseExecution = new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)

        tournamentService = new TournamentService()
        student = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)

        topic = new Topic(course, topicDto)
        topicList = new ArrayList()
        topicList.add(topic)

        startingDate = LocalDateTime.now().format(formatter)
        conclusionDate = LocalDateTime.now().plusDays(1).format(formatter)

        def tournamentDto1 = new TournamentDto("T1",student.getId(),topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto2 = new TournamentDto("T2",student.getId(),topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto3 = new TournamentDto("T3",student.getId(),topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)
        def tournamentDto4 = new TournamentDto("T4",student.getId(),topicList,NUMBER_OF_QUESTIONS,startingDate,conclusionDate)


    }

    def "All tournaments are open"(){
        given: "several open tournaments"
        tournamentDto1.setStatus("open")
        tournamentDto2.setStatus("open")
        tournamentDto3.setStatus("open")
        tournamentDto4.setStatus("open")

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "The returned data is correct"
        result.size() == 4
        result.get(0).title == "T1"
        result.get(1).title == "T2"
        result.get(2).title == "T3"
        result.get(3).title == "T4"


    }

    def "No tournaments are open"(){
        given: "Only close tournaments"
        tournamentDto1.setStatus("closed")
        tournamentDto2.setStatus("closed")
        tournamentDto3.setStatus("closed")
        tournamentDto4.setStatus("closed")

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Both open and close tournaments exist"(){
        given: "Open and closed tournaments"
        tournamentDto1.setStatus("open")
        tournamentDto2.setStatus("closed")
        tournamentDto3.setStatus("open")
        tournamentDto4.setStatus("closed")

        when:
        def result = tournamentService.ShowAllOpenTournaments()

        then: "Only the open tournaments are returned"

        result.size() == 2
        result.get(0).title == "T1"
        result.get(1).title == "T3"

    }




}