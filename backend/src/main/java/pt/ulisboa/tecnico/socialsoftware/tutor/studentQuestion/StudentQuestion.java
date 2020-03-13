package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Column(unique=true, nullable = false)
    private Integer key;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    private String title;

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

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "studentQuestion")
    private Question correspondingQuestion;

    public StudentQuestion() {}

    public StudentQuestion(Course course, User user, StudentQuestionDto stQuestionDto) {
        if (stQuestionDto.getTitle() == null ||
                stQuestionDto.getTitle().trim().length() == 0 ||
                stQuestionDto.getContent() == null ||
                stQuestionDto.getContent().trim().length() == 0 ||
                stQuestionDto.getOptions().stream().anyMatch(optionStr -> optionStr.trim().length() == 0) ||
                stQuestionDto.getOptions().size() != 4 ||
                stQuestionDto.getCorrect() > 4 ||
                stQuestionDto.getCorrect() < 1) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (stQuestionDto.getCorrect() == null) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        this.key = stQuestionDto.getKey();
        this.course = course;
        this.student = user;
        this.title = stQuestionDto.getTitle();
        this.content = stQuestionDto.getContent();
        this.correct = stQuestionDto.getCorrect();

        if (stQuestionDto.getImage() != null) {
            Image img = new Image(stQuestionDto.getImage());
            setImage(img);
            img.setStudentQuestion(this);
        }

        this.options.addAll(stQuestionDto.getOptions());
    }


    public Integer getId() {
        return id;
    }

    public Integer getKey() {
        return key;
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

    public String getCorrectOption() {
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

    public Question getCorrespondingQuestion() {
        return correspondingQuestion;
    }

    public void setCorrespondingQuestion(Question correspondingQuestion) {
        this.correspondingQuestion = correspondingQuestion;
        correspondingQuestion.setStudentQuestion(this);
    }

    public void evaluateStudentQuestion(State result, String justification) {
        if (this.state != State.AWAITING_APPROVAL)
            throw new TutorException(QUESTION_ALREADY_EVALUATED);

        else if (justification != null && justification.trim().length() == 0)
            throw new TutorException(JUSTIFICATION_MISSING_DATA);

        else if (justification != null)
            setJustification(justification);

        else if (result == State.REJECTED)
            throw new TutorException(JUSTIFICATION_MISSING_DATA);

        setState(result);
        if (result == State.APPROVED) {
            QuestionDto questionDto = new QuestionDto(this);
            Question q = new Question(this.course, questionDto);
            setCorrespondingQuestion(q);
        }
    }
}