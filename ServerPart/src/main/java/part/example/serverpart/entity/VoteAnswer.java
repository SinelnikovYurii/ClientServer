package part.example.serverpart.entity;

import java.util.ArrayList;
import java.util.List;

public class VoteAnswer {

    String name;
    int voteCount = 0;
    List<User> votedUsers = new ArrayList<>();

    public VoteAnswer() {

    }

    public VoteAnswer(String name) {
        this.name = name;

    }

    public int getVoteCount(){
        return votedUsers.size();
    }

    public void setVoteCount(int voteCount){
        this.voteCount = voteCount;
    }

    public String getName() {
        return name;
    }
    public void vote(User user) {
        votedUsers.add(user);

    }



}
