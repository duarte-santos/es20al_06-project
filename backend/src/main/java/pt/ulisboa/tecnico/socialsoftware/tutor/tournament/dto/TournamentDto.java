package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import org.springframework.data.annotation.Transient;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.domain.Tournament;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TournamentDto implements Serializable{

    private Integer id;
    private String title;
    private Integer userId;
    private List<Topic> topicList = new ArrayList<>();
    private Integer numberOfQuestions;
    private String startingDate;
    private String conclusionDate;
    private String status = "closed";

    @Transient
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public TournamentDto(){
    }

    public TournamentDto(Tournament tournament){
        this.id = tournament.getId();
        this.title = tournament.getTitle();
        this.userId = tournament.getUserId();
        this.topicList = tournament.getTopicList();
        this.numberOfQuestions = tournament.getNumberOfQuestions();
        this.startingDate = tournament.getStartingDate().format(formatter);
        this.conclusionDate = tournament.getConclusionDate().format(formatter);
        this.status = tournament.getStatus();
    }

    public TournamentDto(String title, Integer id, List<Topic> topicList, Integer numOfQuestions, String startingDate, String conclusionDate){
        this.title = title;
        this.userId = id;
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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public List<Topic> getTopicList() {
        return topicList;
    }

    public String getStatus(){ return this.status; }

    public void setTopicList(List<Topic> topicList) {
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

    public void setStatus(String status){ this.status = status; }

    public LocalDateTime getStartingDateDate() {
        if (getStartingDate() == null || getStartingDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getStartingDate(), formatter);
    }

    public LocalDateTime getConclusionDateDate() {
        if (getConclusionDate() == null || getConclusionDate().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(getConclusionDate(), formatter);
    }
}
