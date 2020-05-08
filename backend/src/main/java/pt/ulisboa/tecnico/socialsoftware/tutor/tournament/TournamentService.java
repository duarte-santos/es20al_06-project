package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

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

import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.repository.TournamentRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
    private QuestionRepository questionRepository;

    @Autowired
    private QuizQuestionRepository quizQuestionRepository;

    @Autowired
    private QuizAnswerRepository quizAnswerRepository;


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
        return tournamentRepository.findAvailable(executionId).stream()
                    .filter(tournament -> DateHandler.now().isBefore(tournament.getConclusionDate()))
                    .filter(tournament -> tournament.getStartingDate().isBefore(DateHandler.now()))
                    .map(TournamentDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<TournamentDto> getDashBoardTournaments(int userId, int executionId){
        return tournamentRepository.findParticipated(userId, executionId).stream()
                .filter(tournament -> DateHandler.now().isAfter(tournament.getConclusionDate()))
                .map(TournamentDto::new).collect(Collectors.toList());


    }



    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void cancelTournament(int tournamentId) {

        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));

        if (tournament.getStartingDate().isBefore(DateHandler.now())){
            throw new TutorException(TOURNAMENT_IS_OPEN, tournamentId);
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

        if (tournament.getStudentList().size() == 2 && tournament.getQuiz() == null && tournament.isOpen()){
            createTournamentQuiz(tournament);
        }

        return new TournamentDto(tournament);
    }

    public void createTournamentQuiz(Tournament tournament){
        Quiz quiz = new Quiz();
        quiz.setType(Quiz.QuizType.GENERATED.toString());
        quiz.setCreationDate(DateHandler.now());

        int executionId = tournament.getCourseExecution().getId();
        CourseExecution courseExecution = courseExecutionRepository.findById(executionId).orElseThrow(() -> new TutorException(COURSE_EXECUTION_NOT_FOUND, executionId));

        List<Question> questions = questionRepository.findAvailableQuestions(courseExecution.getCourse().getId());
        // Get only questions with the correct topics
        List<Question> availableQuestions =  parseQuestions(questions, tournament);
        if (availableQuestions.size() < tournament.getNumberOfQuestions()) {
            throw new TutorException(NOT_ENOUGH_QUESTIONS);
        }

        quiz.generate(availableQuestions);
        quiz.setTitle(tournament.getTitle() + " (Quiz)");
        quiz.setAvailableDate(tournament.getStartingDate());
        quiz.setConclusionDate(tournament.getConclusionDate());
        quiz.setCourseExecution(courseExecution);
        courseExecution.addQuiz(quiz);

        quiz.setTournament(tournament);
        quizRepository.save(quiz);
        tournament.setQuiz(quiz);
    }

    public List<Question> parseQuestions(List<Question> questions, Tournament tournament){
        List<Topic> topics = tournament.getTopicList();
        List<Question> availableQuestions = new ArrayList<>();
        for (Question q : questions){
            for (Topic t : q.getTopics()){
                if (topics.contains(t)){
                    if (q.getImage() == null) // Images bug locally
                        availableQuestions.add(q);
                    if (availableQuestions.size()==tournament.getNumberOfQuestions())
                        return availableQuestions;
                    continue;
                }
            }
        }

        return availableQuestions;
    }


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

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StatementQuizDto startTournament(int userId, int tournamentId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));
        Tournament tournament = tournamentRepository.findById(tournamentId).orElseThrow(() -> new TutorException(TOURNAMENT_NOT_FOUND, tournamentId));
        Quiz quiz = tournament.getQuiz();
        LocalDateTime now = LocalDateTime.now();

        // Check if quiz can be generated
        if (tournament.getStudentList().size() < 2)
            throw new TutorException(QUIZ_CANT_BE_GENERATED);

        // Generate quiz if it hasn't been generated already
        if (tournament.getQuiz() == null)
            createTournamentQuiz(tournament);

        // Check if user in enrolled
        if (!tournament.getStudentList().contains(user))
            throw new TutorException(STUDENT_NOT_ENROLLED);

        // Check if tournament hasn't started
        if (tournament.getStartingDate().isAfter(now))
            throw new TutorException(TOURNAMENT_NOT_OPEN);

        // Check if tournament is already finished
        if (tournament.getConclusionDate().isBefore(now))
            throw new TutorException(TOURNAMENT_IS_FINISHED);

        // Check if user has already participated in the tournament
        if (tournament.getAnsweredList().contains(user))
            throw new TutorException(STUDENT_ALREADY_PARTICIPATED);

        tournament.getAnsweredList().add(user);

        QuizAnswer quizAnswer = new QuizAnswer(user, quiz);
        quizAnswerRepository.save(quizAnswer);
        StatementQuizDto statementQuizDto = new StatementQuizDto(quizAnswer);

        return statementQuizDto;
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void changePrivacy(int userId, String privacy) {

        User user =  userRepository.findById(userId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, userId));

        user.setPrivacy(privacy);
    }
}
