package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import java.io.Serializable;
import java.util.*;

import static pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion.StudentQuestion.State.*;

public class StudentQuestionDto implements Serializable {

    private Integer id;
    private Integer key;
    private String title;
    private String content;
    private Integer correct = 0;
    private List<String> options = new ArrayList<>();
    private ImageDto image;
    private Set<String> topics = new HashSet<>();
    private StudentQuestion.State state = AWAITING_APPROVAL;
    private QuestionDto correspondingQuestion;

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
        this.state = stQuestion.getState();
        if (stQuestion.getCorrespondingQuestion() != null)
            this.correspondingQuestion = new QuestionDto(stQuestion.getCorrespondingQuestion());
    }

    public StudentQuestionDto(Integer key, String title, String content, Integer correct, List<String> options) {
        this.key = key;
        this.title = title;
        this.content = content;
        this.correct = correct;
        this.options.addAll(options);
    }

    public StudentQuestionDto(Integer key, String title, String content, Integer correct, List<String> options, ImageDto img) {
        this.key = key;
        this.title = title;
        this.content = content;
        this.correct = correct;
        this.options.addAll(options);
        this.image = img;
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

    public StudentQuestion.State getState() {
        return state;
    }

    public void setState(StudentQuestion.State state) {
        this.state = state;
    }

    public QuestionDto getCorrespondingQuestion() {
        return correspondingQuestion;
    }

    public void setCorrespondingQuestion(QuestionDto correspondingQuestion) {
        this.correspondingQuestion = correspondingQuestion;
    }
}