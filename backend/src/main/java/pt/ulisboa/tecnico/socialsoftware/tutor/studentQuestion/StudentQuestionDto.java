package pt.ulisboa.tecnico.socialsoftware.tutor.studentQuestion;

import pt.ulisboa.tecnico.socialsoftware.tutor.course.Course;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Image;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.OptionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.Quiz;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.domain.QuizQuestion;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StudentQuestionDto implements Serializable {
    public enum State {
        AWAITING_APPROVAL, APPROVED, REJECTED
    }

    private Integer id;
    private String title;
    private String content;
    private Integer correct;
    private List<String> options = new ArrayList<>();
    private ImageDto image;
    private Set<String> topics = new HashSet<>();
    private String state;
    //private String justification; // TODO approval/rejection class?
    private QuestionDto correspondingQuestion; //TODO necessario?

    public StudentQuestionDto() {
    }

    public StudentQuestionDto(StudentQuestion stQuestion) {
        this.id = stQuestion.getId();
        this.title = stQuestion.getTitle();
        this.content = stQuestion.getContent();
        this.correct = stQuestion.getCorrect();
        this.options.addAll(stQuestion.getOptions());
        if (stQuestion.getImage() != null)
            this.image = new ImageDto(stQuestion.getImage());
        this.topics.addAll(stQuestion.getTopics());
        this.state = stQuestion.getState().name();
        this.correspondingQuestion = new QuestionDto(stQuestion.getCorrespondingQuestion());
    }

    public StudentQuestionDto(String title, String content, Integer correct, List<String> options) {
        this.title = title;
        this.content = content;
        this.correct = correct;
        this.options.addAll(options);
        this.state = "AWAITING_APPROVAL";
    }

    public StudentQuestionDto(String title, String content, Integer correct, List<String> options, ImageDto img) {
        this.title = title;
        this.content = content;
        this.correct = correct;
        this.options.addAll(options);
        this.state = "AWAITING_APPROVAL";
        this.image = img;
    }

    public Integer getId() {
        return id;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public QuestionDto getCorrespondingQuestion() {
        return correspondingQuestion;
    }

    public void setCorrespondingQuestion(QuestionDto correspondingQuestion) {
        this.correspondingQuestion = correspondingQuestion;
    }
}