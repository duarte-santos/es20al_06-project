package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.ImageRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.TopicRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Service
public class StudentQuestionService {

    @Autowired
    private StudentQuestionRepository studentQuestionRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto createStudentQuestion(int courseId, int studentId, StudentQuestionDto studentQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        StudentQuestion stQuestion = new StudentQuestion(course, student, studentQuestionDto);

        studentQuestionRepository.save(stQuestion);
        return new StudentQuestionDto(stQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> findStudentQuestions(int courseId) {
        return studentQuestionRepository.findStudentQuestions(courseId).stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto findStudentQuestionById(Integer stQuestionId) {
        return studentQuestionRepository.findById(stQuestionId).map(StudentQuestionDto::new)
                .orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, stQuestionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto findStudentQuestionByKey(Integer key) {
        return studentQuestionRepository.findByKey(key).map(StudentQuestionDto::new)
                .orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, key));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto evaluateStudentQuestion(Integer studentQuestionId, StudentQuestionDto studentQuestionDto) {

        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionId)
                .orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        studentQuestion.evaluateStudentQuestion(studentQuestionDto);

        if (studentQuestion.getState().name().equals("APPROVED") && studentQuestion.getCorrespondingQuestionKey() == null) {
            Question question = new Question(studentQuestion);
            question.updateTopics(studentQuestion.getTopics().stream().map(topic -> topicRepository.findTopicByName(question.getCourse().getId(), topic)).collect(Collectors.toSet()));
            studentQuestion.setCorrespondingQuestionKey(question.getKey());
            questionRepository.save(question);
        }

        studentQuestionRepository.save(studentQuestion);
        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> viewOwnStudentQuestions(int studentId) {
        return studentQuestionRepository.viewStudentQuestions(studentId).stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public CourseDto findStudentQuestionCourse(Integer studentQuestionId) {
        return studentQuestionRepository.findById(studentQuestionId)
                .map(StudentQuestion::getCourse)
                .map(CourseDto::new)
                .orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateStudentQuestionTopics(Integer studentQuestionId, String[] topics) {
        StudentQuestion stQuestion = studentQuestionRepository.findById(studentQuestionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        stQuestion.setTopics(Arrays.stream(topics).collect(Collectors.toSet()));
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void updateStudentQuestionImage(Integer studentQuestionId, String type) {
        StudentQuestion stQuestion = studentQuestionRepository.findById(studentQuestionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, studentQuestionId));

        Image image = stQuestion.getImage();
        if (image == null) {
            image = new Image();
            stQuestion.setImage(image);
            imageRepository.save(image);
        }
        stQuestion.getImage().setUrl(stQuestion.getCourse().getName().replaceAll("\\s", "") +
                stQuestion.getCourse().getType() +
                stQuestion.getKey() +
                "." + type);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeStudentQuestion(Integer stQuestionId) {
        StudentQuestion stQuestion = studentQuestionRepository.findById(stQuestionId).orElseThrow(() -> new TutorException(STUDENT_QUESTION_NOT_FOUND, stQuestionId));

        if (stQuestion.getState().name().equals("APPROVED")) {
            Question question = questionRepository.findByKey(stQuestion.getCorrespondingQuestionKey()).orElse(null);
            if (question != null) {
                question.remove();
                questionRepository.delete(question);
            }
        }

        stQuestion.remove();
        studentQuestionRepository.delete(stQuestion);
    }

}
