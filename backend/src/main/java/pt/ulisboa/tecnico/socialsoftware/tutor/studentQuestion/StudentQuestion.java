package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.*;
import java.util.stream.Collectors;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
@Table(
        name = "student_questions",
        indexes = {
                @Index(name = "student_question_indx_0", columnList = "key")
        })
public class StudentQuestion {
    public enum State {
        AWAITING_APPROVAL, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer key;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "integer default 0")
    private Integer correct = 0;

    @ElementCollection
    private List<String> options = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "studentQuestion")
    private Image image;

    @ElementCollection
    private Set<String> topics = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private State state = State.AWAITING_APPROVAL;

    private String justification;

    private Integer correspondingQuestionKey;

    public StudentQuestion() {}

    public StudentQuestion(Course course, User user, StudentQuestionDto stQuestionDto) {
        checkConsistentStudentQuestion(stQuestionDto);

        this.key = stQuestionDto.getKey();
        this.student = user;
        this.title = stQuestionDto.getTitle();
        this.content = stQuestionDto.getContent();
        this.correct = stQuestionDto.getCorrect();

        this.course = course;
        course.addStudentQuestion(this);

        if (stQuestionDto.getImage() != null) {
            createImage(stQuestionDto.getImage());
        }

        this.options.addAll(stQuestionDto.getOptions());
        this.topics.addAll(stQuestionDto.getTopics());

        this.justification = stQuestionDto.getJustification();
    }

    public StudentQuestion(Course course, User user, Integer key, String title, String content, List<String> options, Integer correct) {
        this.course = course;
        this.student = user;
        this.key = key;
        this.title = title;
        this.content = content;
        this.options.addAll(options);
        this.correct = correct;
    }

    private void createImage(ImageDto imageDto) {
        Image img = new Image(imageDto);
        setImage(img);
        img.setStudentQuestion(this);
    }

    private void checkConsistentStudentQuestion(StudentQuestionDto stQuestionDto) {
        if (stQuestionDto.getTitle() == null ||
                stQuestionDto.getTitle().trim().length() == 0 ||
                stQuestionDto.getContent() == null ||
                stQuestionDto.getContent().trim().length() == 0 ||
                stQuestionDto.getOptions().stream().anyMatch(optionStr -> optionStr == null) ||
                stQuestionDto.getOptions().stream().anyMatch(optionStr -> optionStr.trim().length() == 0) ||
                stQuestionDto.getOptions().size() != 4) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (stQuestionDto.getCorrect() == null) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        if (stQuestionDto.getCorrect() > 4 ||
                stQuestionDto.getCorrect() < 1) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }
    }


    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        if (this.key == null)
            generateKeys();

        return key;
    }

    private void generateKeys() {
        Integer max = this.course.getStudentQuestions().stream()
                .filter(stQuestion -> stQuestion.key != null)
                .map(StudentQuestion::getKey)
                .max(Comparator.comparing(Integer::valueOf))
                .orElse(0);

        List<StudentQuestion> nullKeyStQuestions = this.course.getStudentQuestions().stream()
                .filter(stQuestion -> stQuestion.key == null).collect(Collectors.toList());

        for (StudentQuestion stQuestion: nullKeyStQuestions) {
            max = max + 1;
            stQuestion.key = max;
        }
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getStudent() {
        return student;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCorrect() {
        return correct;
    }

    public String correctOption() {
        return options.get(correct - 1);
    }

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
        image.setStudentQuestion(this);
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void addTopics(String topic) {
        this.topics.add(topic);
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public Integer getCorrespondingQuestionKey() {
        return correspondingQuestionKey;
    }

    public void setCorrespondingQuestionKey(Integer correspondingQuestionKey) {
        this.correspondingQuestionKey = correspondingQuestionKey;
    }

    public void evaluateStudentQuestion(State result, String justification) {
        checkValidEvaluation(result, justification);

        if (justification != null)
            setJustification(justification);
        setState(result);

        if (result == State.APPROVED) {
            createCorrespondingQuestion();
        }
    }

    private void createCorrespondingQuestion() {
        QuestionDto questionDto = new QuestionDto(this);
        Question question = new Question(this.course, questionDto);
        this.setCorrespondingQuestionKey(question.getKey());
    }

    private void checkValidEvaluation(State result, String justification) {
        if (this.state != State.AWAITING_APPROVAL)
            throw new TutorException(QUESTION_ALREADY_EVALUATED);

        if (justification == null && result == State.REJECTED ||
                justification != null && justification.trim().length() == 0)
            throw new TutorException(JUSTIFICATION_MISSING_DATA);
    }
}