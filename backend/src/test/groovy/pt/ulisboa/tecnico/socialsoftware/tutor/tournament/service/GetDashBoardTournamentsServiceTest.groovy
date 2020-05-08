package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository
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

@DataJpaTest
class GetDashBoardTournamentsServiceTest extends Specification{

    static final String TOURNAMENT_TITLE = "T12"
    static final String STUDENT_NAME = "StudentName"
    static final String STUDENT_NAME2 = "StudentName2"
    static final String USERNAME = "StudentUsername"
    static final String USERNAME2 = "StudentUsername2"
    static final String COURSE_NAME = "Software Architecture"
    static final String ACRONYM = "AS1"
    static final String ACADEMIC_TERM = "1 SEM"
    static final String TOPIC_NAME = "TopicName"
    static final int NUMBER_OF_QUESTIONS = 1
    static final String YESTERDAY = DateHandler.toISOString(DateHandler.now().minusDays(1))
    static final String TOMORROW = DateHandler.toISOString(DateHandler.now().plusDays(1))
    static final String EARLIER = DateHandler.toISOString(DateHandler.now().minusDays(2))

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

    @Autowired
    CourseExecutionRepository courseExecutionRepository


    def creator
    def static enrollingStudent
    def course
    def static execution
    def topic
    def topicDto
    def static topicList
    def static topicDtoList
    def studentId
    def tournament1
    def tournament2
    def tournament3

    private static Tournament createTournament(String title, List<TopicDto> topicList, Integer numOfQuestions, String startingDate, String conclusionDate){
        def tournamentDto = new TournamentDto(title, topicList, numOfQuestions, startingDate, conclusionDate)
        def tournament = new Tournament(tournamentDto)
        tournament.setCourseExecution(execution)
        return tournament
    }

    def setup(){

        course = new Course(COURSE_NAME, Course.Type.TECNICO)
        execution =  new CourseExecution(course, ACRONYM, ACADEMIC_TERM, Course.Type.TECNICO)
        courseRepository.save(course)
        courseExecutionRepository.save(execution)

        creator = new User(STUDENT_NAME, USERNAME, 1, User.Role.STUDENT)
        userRepository.save(creator)

        enrollingStudent = new User(STUDENT_NAME2, USERNAME2, 2, User.Role.STUDENT)
        userRepository.save(enrollingStudent)
        studentId = enrollingStudent.getId()

        topicDto = new TopicDto()
        topicDto.setName(TOPIC_NAME)
        topic = new Topic(course, topicDto)
        topicRepository.save(topic)

        topicDtoList = new ArrayList()
        topicDtoList.add(topicDto)
        topicList = new ArrayList()
        topicList.add(topic)

        tournament1 = createTournament("T1", topicList, NUMBER_OF_QUESTIONS, EARLIER, TOMORROW)
        tournament2 = createTournament("T2", topicList, NUMBER_OF_QUESTIONS, EARLIER, TOMORROW)
        tournament3 = createTournament("T3", topicList, NUMBER_OF_QUESTIONS, EARLIER, TOMORROW)

        tournament1.setCourseExecution(execution)
        tournament2.setCourseExecution(execution)
        tournament3.setCourseExecution(execution)


    }

    def "Student participates in 3 tournaments but only 2 have ended"(){
        given: "several open tournaments"

        tournament1.getAnsweredList().add(enrollingStudent)
        tournament2.getAnsweredList().add(enrollingStudent)
        tournament3.getAnsweredList().add(enrollingStudent)

        tournament1.setConclusionDate(DateHandler.toLocalDateTime(YESTERDAY))
        tournament3.setConclusionDate(DateHandler.toLocalDateTime(YESTERDAY))

        enrollingStudent.getTournamentsAnswered().add(tournament1);
        enrollingStudent.getTournamentsAnswered().add(tournament2);
        enrollingStudent.getTournamentsAnswered().add(tournament3);

        tournamentRepository.save(tournament1)
        tournamentRepository.save(tournament2)
        tournamentRepository.save(tournament3)

        when:
        def result = tournamentService.getDashBoardTournaments(studentId, execution.getId())

        then: "The returned data is correct"
        //Tournament 2 is still open so it should not be returned
        result.size() == 2
        result.get(0).getId()==tournament1.getId()
        result.get(1).getId()==tournament3.getId()

    }

    def "Student didnt participate in any tournaments"(){
        given: "Only closed tournaments"

        tournamentRepository.save(tournament1)
        tournamentRepository.save(tournament2)
        tournamentRepository.save(tournament3)

        when:
        def result = tournamentService.getDashBoardTournaments(studentId, execution.getId())

        then: "Empty list is returned"
        result.size() == 0
    }


    @TestConfiguration
    static class TournamentServiceImplTestContextConfiguration {

        @Bean
        TournamentService TournamentService() {
            return new TournamentService()
        }
    }


}