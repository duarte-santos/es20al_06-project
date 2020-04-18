package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.*;

public class StudentQuestionDto implements Serializable {

    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private List<String> options = new ArrayList<>();
    private Integer correct = 0;
    private ImageDto image;
    private Set<String> topics = new HashSet<>();
    private String state = StudentQuestion.State.AWAITING_APPROVAL.name();
    private String justification;
    private UserDto student;

    public StudentQuestionDto() {
    }

    public StudentQuestionDto(StudentQuestion stQuestion) {
        this.id = stQuestion.getId();
        this.key = stQuestion.getKey();
        this.title = stQuestion.getTitle();
        this.content = stQuestion.getContent();
        this.correct = stQuestion.getCorrect();
        this.options.addAll(stQuestion.getOptions());
        if (stQuestion.getImage() != null)
            this.image = new ImageDto(stQuestion.getImage());
        this.topics.addAll(stQuestion.getTopics());
        this.state = stQuestion.getState().name();
        this.justification = stQuestion.getJustification();
        if (stQuestion.getStudent() != null)
            this.student = new UserDto(stQuestion.getStudent());
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

    public void setCorrect(Integer correct) {
        this.correct = correct;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public ImageDto getImage() {
        return image;
    }

    public void setImage(ImageDto image) {
        this.image = image;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void addTopic(String topic) {
        this.topics.add(topic);
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public UserDto getStudent() { return student;}

    public void setStudent(UserDto student) {
        this.student = student;
    }

}