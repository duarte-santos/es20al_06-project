package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.*;

@Entity
public class StudentQuestion {
    public enum State {
        AWAITING_APPROVAL, APPROVED, REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //private Integer key;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User student;

    private String title;

    private String content;

    @Column(columnDefinition = "integer default 1")
    private Integer correct;

    @ElementCollection
    private List<String> options = new ArrayList<>();

    @OneToOne
    private Image image;

    @ElementCollection
    private Set<String> topics = new HashSet<>();

    @Enumerated(EnumType.STRING)
    private State state = State.AWAITING_APPROVAL;

    @OneToOne
    private Question correspondingQuestion; //TODO necessario?

    //private String justification; // TODO approval/rejection class?

    public StudentQuestion() {}

    public StudentQuestion(Course course, User user, StudentQuestionDto stQuestionDto) {
        if (stQuestionDto.getTitle().trim().length() == 0 ||
                stQuestionDto.getTitle() == null ||
                stQuestionDto.getContent().trim().length() == 0 ||
                stQuestionDto.getContent() == null ||
                stQuestionDto.getOptions().stream().anyMatch(optionStr -> optionStr.trim().length() == 0) ||
                stQuestionDto.getOptions().size() != 4 ||
                stQuestionDto.getCorrect() > stQuestionDto.getOptions().size() || stQuestionDto.getCorrect() < 1) {
            throw new TutorException(QUESTION_MISSING_DATA);
        }

        if (stQuestionDto.getCorrect() == null) {
            throw new TutorException(QUESTION_MULTIPLE_CORRECT_OPTIONS);
        }

        this.course = course;
        this.student = user;
        this.title = stQuestionDto.getTitle();
        this.content = stQuestionDto.getContent();
        this.correct = stQuestionDto.getCorrect();

        if (stQuestionDto.getImage() != null) {
            this.image = new Image(stQuestionDto.getImage());
        }

        this.options.addAll(stQuestionDto.getOptions());
    }


    public Integer getId() {
        return id;
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

    public Question getCorrespondingQuestion() {
        return correspondingQuestion;
    }

    public void setCorrespondingQuestion(Question correspondingQuestion) {
        this.correspondingQuestion = correspondingQuestion;
    }
}