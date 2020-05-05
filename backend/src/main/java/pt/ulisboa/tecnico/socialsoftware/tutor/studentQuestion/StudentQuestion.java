package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
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

    @Column(columnDefinition = "TEXT")
    private String justification;

    private Integer correspondingQuestionKey;

    public StudentQuestion() {}

    public StudentQuestion(Course course, User user, StudentQuestionDto stQuestionDto) {
        checkConsistentStudentQuestion(stQuestionDto);
        stQuestionDto = trimStudentQuestion(stQuestionDto);

        this.key = stQuestionDto.getKey();
        this.student = user;
        this.title = stQuestionDto.getTitle();
        this.content = stQuestionDto.getContent();
        this.correct = stQuestionDto.getCorrect();

        setCourse(course);

        if (stQuestionDto.getImage() != null) {
            createImage(stQuestionDto.getImage());
        }

        this.options.addAll(stQuestionDto.getOptions());
        this.topics.addAll(stQuestionDto.getTopics());

        this.state = State.valueOf(stQuestionDto.getState());

        this.justification = stQuestionDto.getJustification();
    }

    // TODO : check and trim parameters or change spock tests
    // Only used in Spock Test
    public StudentQuestion(Course course, User user, String title, String content, List<String> options, Integer correct) {
        setCourse(course);
        this.student = user;
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
                stQuestionDto.getTitle().trim().length() == 0) {
            throw new TutorException(SQ_TITLE_MISSING_DATA);
        }
        if (stQuestionDto.getContent() == null ||
                stQuestionDto.getContent().trim().length() == 0) {
            throw new TutorException(SQ_CONTENT_MISSING_DATA);
        }
        if (stQuestionDto.getOptions().stream().anyMatch(Objects::isNull) ||
                stQuestionDto.getOptions().stream().anyMatch(optionStr -> optionStr.trim().length() == 0)) {
            throw new TutorException(SQ_OPTION_MISSING_DATA);
        }
        if (stQuestionDto.getOptions().size() != 4) {
            throw new TutorException(STUDENT_QUESTION_INVALID_OPTIONS_AMOUNT);
        }
        if (stQuestionDto.getCorrect() == null ||
                stQuestionDto.getCorrect() > 4 ||
                stQuestionDto.getCorrect() < 1) {
            throw new TutorException(SQ_INVALID_CORRECT_OPTION);
        }
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        if (this.key == null)
            generateKeys();

        return key;
    }

    private void generateKeys() {
        int max = this.course.getStudentQuestions().stream()
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
        course.addStudentQuestion(this);
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

    public void setTopics(Set<String> topics) {
        this.topics = topics;
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

    public void evaluateStudentQuestion(StudentQuestionDto studentQuestionDto) {
        State newState = State.valueOf(studentQuestionDto.getState());
        String newJustification = studentQuestionDto.getJustification();

        if (newJustification != null && newJustification.trim().length() == 0)
            newJustification = null; /*blank justifications are treated as null*/

        checkValidEvaluation(newState, newJustification);

        if (newJustification != null)
            setJustification(newJustification);
        setState(newState);
    }

    private void checkValidEvaluation(State newState, String newJustification) {
        if (this.state != State.AWAITING_APPROVAL)
            throw new TutorException(STUDENT_QUESTION_ALREADY_EVALUATED);

        if (newState == State.REJECTED && newJustification == null)
            throw new TutorException(JUSTIFICATION_MISSING_DATA);
    }

    public void remove() {
        getCourse().getStudentQuestions().remove(this);
        course = null;
        getOptions().clear();
        getTopics().clear();
    }

    public void editStudentQuestion(StudentQuestionDto studentQuestionDto) {
        if (state != State.REJECTED)
            throw new TutorException(CANNOT_EDIT_STUDENT_QUESTION);

        checkConsistentStudentQuestion(studentQuestionDto);
        studentQuestionDto = trimStudentQuestion(studentQuestionDto);

        this.title = studentQuestionDto.getTitle();
        this.content = studentQuestionDto.getContent();
        this.options = studentQuestionDto.getOptions();
        this.correct = studentQuestionDto.getCorrect();

        this.justification = null;
        this.state = State.AWAITING_APPROVAL;
    }

    public StudentQuestionDto trimStudentQuestion(StudentQuestionDto studentQuestionDto) {
        studentQuestionDto.setTitle(studentQuestionDto.getTitle().trim());
        studentQuestionDto.setContent(studentQuestionDto.getContent().trim());

        List<String> optionList = studentQuestionDto.getOptions();
        for (int i = 0; i < optionList.size(); i++) {
            optionList.set(i, optionList.get(i).trim());
        }
        studentQuestionDto.setOptions(optionList);

        return studentQuestionDto;
    }
}