package pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TournamentDto implements Serializable{

    private String title;
    private Integer userId;
    private List<Topic> topicList = new ArrayList<>();
    private Integer numberOfQuestions;
    private String startingDate;
    private String conclusionDate;
    private String status = "closed";

    public TournamentDto(){
    }

    public TournamentDto(String t, Integer id, List<Topic> tl, Integer num, String sd, String cd){
        this.title = t;
        this.userId = id;
        this.topicList = tl;
        this.numberOfQuestions = num;
        this.startingDate = sd;
        this.conclusionDate = cd;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getConclusionDate() {
        return conclusionDate;
    }

    public void setConclusionDate(String conclusionDate) {
        this.conclusionDate = conclusionDate;
    }

    public void setStatus(String status){ this.status = status; }
}
