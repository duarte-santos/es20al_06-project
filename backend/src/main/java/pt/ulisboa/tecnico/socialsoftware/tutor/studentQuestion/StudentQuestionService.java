package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.repository.QuestionRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.course.CourseRepository;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import java.sql.SQLException;
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

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto createStudentQuestion(int courseId, int studentId, StudentQuestionDto studentQuestionDto) {
        Course course = courseRepository.findById(courseId).orElseThrow(() -> new TutorException(COURSE_NOT_FOUND, courseId));
        User student = userRepository.findById(studentId).orElseThrow(() -> new TutorException(USER_NOT_FOUND, studentId));

        if (studentQuestionDto.getKey() == null) {
            int maxStQuestionNumber = studentQuestionRepository.getMaxStudentQuestionNumber() != null ?
                    studentQuestionRepository.getMaxStudentQuestionNumber() : 0;
            int maxQuestionNumber = questionRepository.getMaxQuestionNumber() != null ?
                    questionRepository.getMaxQuestionNumber() : 0;
            studentQuestionDto.setKey(Math.max(maxStQuestionNumber, maxQuestionNumber) + 1);
        }

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
    public StudentQuestionDto findStudentQuestionByKey(Integer key) {
        return studentQuestionRepository.findByKey(key).map(StudentQuestionDto::new)
                .orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, key));
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestionDto evaluateStudentQuestion(StudentQuestion.State state, String justification, StudentQuestionDto studentQuestionDto) {
        StudentQuestion studentQuestion = studentQuestionRepository.findById(studentQuestionDto.getId()).orElseThrow(() -> new TutorException(QUESTION_NOT_FOUND, studentQuestionDto.getId()));
        studentQuestion.evaluateStudentQuestion(state, justification);
        studentQuestionRepository.save(studentQuestion);
        return new StudentQuestionDto(studentQuestion);
    }

    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public List<StudentQuestionDto> findStudentQuestionsFromStudent(int studentId) {

        return studentQuestionRepository.findStudentQuestionsFromStudent(studentId).stream().map(StudentQuestionDto::new).collect(Collectors.toList());
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public StudentQuestion.State findSpecificStudentQuestionState(int studentId, int id) {

        return (studentQuestionRepository.findSpecificStudentQuestion(studentId, id)).getState();
    }


    @Retryable(
            value = { SQLException.class },
            backoff = @Backoff(delay = 5000))
    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public String findSpecificStudentQuestionJustification(int studentId, int id) {

        return (studentQuestionRepository.findSpecificStudentQuestion(studentId, id)).getJustification();
    }

}
