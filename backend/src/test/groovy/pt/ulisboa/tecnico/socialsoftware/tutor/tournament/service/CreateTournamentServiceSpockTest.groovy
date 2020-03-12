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


class CreateTournamentServiceSpockTest extends Specification{

    static final String STUDENT_NAME = "StudentName"
    static final String USERNAME = "StudentUsername"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1st Semester"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1
    static final String TOURNAMENT_TITLE = "T12"


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

    }

    def "a student creates a tournament"(){
        given: "A student, topic list, tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "The returned data is correct"
        result.title == TOURNAMENT_TITLE
        result.userId == student.getId()
        result.topicList == topicList
        result.numberOfQuestions == NUMBER_OF_QUESTIONS
        result.startingDate == startingDate
        result.conclusionDate == conclusionDate
    }

    def "choose number of questions smaller than 1"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with blank title"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle("       ")
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with null title"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(null)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with empty topicList"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        def topicList2 = new ArrayList()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList2)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with null topicList"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(null)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(null)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with null dates"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(null)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(null)
        tournamentDto.setConclusionDate(null)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with invalid dates (Dates in the past) "(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        def startingDate2 = LocalDateTime.now().minusDays(2)
        def conclusionDate2 = LocalDateTime.now().minusDays(1)
        tournamentDto.setTitle(null)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate2.format(formatter))
        tournamentDto.setConclusionDate(conclusionDate2.format(formatter))

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "create a tournament with starting hour greater than finishing hour"(){

        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList)
        tournamentDto.setNumberOfQuestions(NUMBER_OF_QUESTIONS)
        tournamentDto.setStartingDate(conclusionDate)
        tournamentDto.setConclusionDate(startingDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }

    def "Create tournament with topics that do not exist"(){
        given: "A tournamentDto"
        def tournamentDto = new TournamentDto()
        def topicList2 = new ArrayList()
        topicList2.add(topic)
        tournamentDto.setTitle(TOURNAMENT_TITLE)
        tournamentDto.setUserId(student.getId())
        tournamentDto.setTopicList(topicList2)
        tournamentDto.setNumberOfQuestions(0)
        tournamentDto.setStartingDate(startingDate)
        tournamentDto.setConclusionDate(conclusionDate)

        when:
        def result = tournamentService.createTournament(tournamentDto)

        then: "An exception is thrown"
        thrown(TutorException)
    }


}