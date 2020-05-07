package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TournamentDto implements Serializable{

    private Integer id;
    private String title;
    private List<TopicDto> topicList = new ArrayList<>();
    private Integer numberOfQuestions;
    private String startingDate;
    private String conclusionDate;
    private List<UserDto> studentList = new ArrayList<>();
    private Integer creatingUserId;
    private Integer quizId;
    private String creatorUsername;

    public TournamentDto(){}

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        this.title = tournament.getTitle();
        this.numberOfQuestions = tournament.getNumberOfQuestions();

        this.startingDate = DateHandler.toISOString(tournament.getStartingDate());
        this.conclusionDate = DateHandler.toISOString(tournament.getConclusionDate());

        if (tournament.getQuiz() != null)
            this.quizId = tournament.getQuiz().getId();

        if (tournament.getCreator() != null){
            this.creatingUserId = tournament.getCreator().getId();
            this.creatorUsername = tournament.getCreator().getUsername();
        }

        List<Topic> topicListAux = tournament.getTopicList();

        for (Topic topic : topicListAux) {
            TopicDto topicdto = new TopicDto(topic);
            this.topicList.add(topicdto);
        }

        setStudentList(tournament.getStudentList().stream().map(UserDto::new).collect(Collectors.toList()));
    }

    public TournamentDto(String title, List<TopicDto> topicList, Integer numOfQuestions, String startingDate, String conclusionDate){
        this.title = title;
        this.topicList = topicList;
        this.numberOfQuestions = numOfQuestions;
        this.startingDate = startingDate;
        this.conclusionDate = conclusionDate;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<TopicDto> getTopicList() {
        return topicList;
    }


    public void setTopicList(List<TopicDto> topicList) {
        this.topicList = topicList;
    }

    public Integer getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(Integer numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }


    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public List<UserDto> getStudentList() {
        return studentList;
    }

    public void setStudentList(List<UserDto> studentList) {
        this.studentList = studentList;
    }

    public Integer getQuizId() {
        return quizId;
    }

    public void setQuizId(Integer quizId) {
        this.quizId = quizId;
    }

    public String getCreatorUsername() {
        return creatorUsername;
    }

    public void setCreatorUsername(String creatorUsername) {
        this.creatorUsername = creatorUsername;
    }

    public Integer getCreatingUserId() {
        return creatingUserId;
    }

    public void setCreatingUserId(Integer creatingUserId) {
        this.creatingUserId = creatingUserId;
    }
}
