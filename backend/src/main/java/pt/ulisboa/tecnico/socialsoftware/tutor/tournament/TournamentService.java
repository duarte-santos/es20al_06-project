package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.QuizAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.repository.QuizAnswerRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecution;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseExecutionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizQuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.repository.QuizRepository;

import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;

import java.util.Set;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;


@Service
public class TournamentService{

    @Autowired
    TournamentRepository tournamentRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CourseExecutionRepository courseExecutionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto createTournament(int executionId, int studentId, TournamentDto tournamentDto){

        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));
        Course course = courseExecution.getCourse();

        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        Tournament tournament = new Tournament(tournamentDto, student);

        tournament.setCourseExecution(courseExecution);

        /* Topics */
        checkTopics(tournamentDto, course);

        List<TopicDto> topics = tournamentDto.getTopicList();
        tournament.updateTopics(topics.stream().map(topicDto -> topicRepository.findTopicByName(course.getId(), topicDto.getName())).collect(Collectors.toSet()));

        tournamentRepository.save(tournament);
        tournamentDto = new TournamentDto(tournament);
        return tournamentDto;
    }

    private void checkTopics(TournamentDto tournament, Course course) {
        if (tournament.getTopicList() == null || tournament.getTopicList().isEmpty()){
            throw new TutorException(TOURNAMENT_TOPIC_LIST_IS_EMPTY);
        }
        for (TopicDto topic : tournament.getTopicList()) {
            Topic topic2 = topicRepository.findTopicByName(course.getId(), topic.getName());
            if (topic2 == null) throw new TutorException(TOURNAMENT_TOPIC_DOESNT_EXIST, topic.getId());
        }
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> showAllOpenTournaments(int executionId){
            return tournamentRepository.findOpen(executionId).stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament(int tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        tournament.removeTopicList();
        tournament.removeUsers();
        tournament.removeCourseExecution();


        Quiz quiz = tournament.getQuiz();
        if (quiz != null){
            quiz.remove();

            Set<QuizQuestion> quizQuestions = new HashSet<>(quiz.getQuizQuestions());

            quizQuestions.forEach(QuizQuestion::remove);
            quizQuestions.forEach(quizQuestion -> quizQuestionRepository.delete(quizQuestion));

            quizRepository.delete(quiz);
            tournament.setQuiz(null);
        }

        tournamentRepository.delete(tournament);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto getTournamentById(int tournamentId) {
        return tournamentRepository.findById(tournamentId)
                .map(TournamentDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public TournamentDto enrollInTournament(int studentId, int tournamentId){

        User user = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        tournament.addStudent(user);
        user.getTournamentsEnrolled().add(tournament);

        if (tournament.getStudentList().size() == 2 && tournament.getQuiz() == null){
            generateTournamentQuiz(tournament, user);
        }

        return new TournamentDto(tournament);
    }

    public void generateTournamentQuiz(Tournament tournament, User user){
        Quiz quiz = new Quiz();
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        int executionId = tournament.getCourseExecution().getId();
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<Question> questions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());
        // Get only questions with the correct topics
        List<Question> availableQuestions =  parseQuestions(questions, tournament);

        /*
        if(quizDetails.getAssessment() != null) {
            availableQuestions = filterByAssessment(availableQuestions, quizDetails);
        }
        // TODO else use default assessment
        */


        if (availableQuestions.size() < tournament.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        // ???
        availableQuestions = user.filterQuestionsByStudentModel(tournament.getNumberOfQuestions(), availableQuestions);

        quiz.generate(availableQuestions);
        quiz.setTitle(tournament.getTitle() + " (Quiz)");
        quiz.setAvailableDate(tournament.getStartingDate());
        quiz.setConclusionDate(tournament.getConclusionDate());


        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quizRepository.save(quiz);
        tournament.setQuiz(quiz);
    }

    public List<Question> parseQuestions(List<Question> questions, Tournament tournament){
        List<Topic> topics = tournament.getTopicList();
        List<Question> availableQuestions = new ArrayList<>();
        for (Question q : questions){
            for (Topic t : q.getTopics()){
                if (topics.contains(t)){
                    availableQuestions.add(q);
                    continue;
                }
            }
        }

        return availableQuestions;
    }

    /*
    public List<Question> parseQuestions(List<Question> questions, Tournament tournament){
        List<Topic> topics = tournament.getTopicList();
        List<Question> availableQuestions = new ArrayList<>();
        boolean next = false;
        for (Question q : questions){
            for (Topic t : q.getTopics()){
                for (Topic tt : topics) {
                    if (t.getId() == tt.getId()) {
                        availableQuestions.add(q);
                        next = true;
                        break;
                    }
                }
                if (next){
                    next = false;
                    break;
                }
            }
        }

        return availableQuestions;
    }
    */


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> showAvailableTournaments(int executionId){
        return tournamentRepository.findAvailable(executionId).stream().map(TournamentDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findTournamentCourseExecution(int tournamentId) {
        return this.tournamentRepository.findById(tournamentId)
                .map(Tournament::getCourseExecution)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
    }
}
