package part.example.serverpart.entity;

import java.util.ArrayList;
import java.util.List;

public class Vote {


    String name;
    String description;
    List<VoteAnswer> answers = new ArrayList<>();

    User creator;
    int answersCount;

    public Vote(){

    }

    public Vote(User creator){
        this.creator = creator;
    }

    public Vote(String name, String description, List<VoteAnswer> answers, int votesCount, User creator) {
        this.name = name;
        this.description = description;
        this.answers = answers;
        this.answersCount = votesCount;
        this.creator = creator;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public void addAnswer(VoteAnswer answer){
        answers.add(answer);
    }

    public void setAnswersCount(int answersCount) {
        this.answersCount = answersCount;
    }

    public int getAnswersCount() {
        return answersCount;
    }

    public List<VoteAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<VoteAnswer> answers) {
        this.answers = answers;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }



}
