package part.example.serverpart.entity;

import java.util.ArrayList;
import java.util.List;

public class Topic {

    String name;
    List<Vote> voteList = new ArrayList<>();

    public Topic(String name) {
        this.name = name;
    }

    public Topic() {

    }

    public Topic(String name, List<Vote> voteList) {
        this.name = name;
        this.voteList = voteList;
    }


    public void DeleteVote(User user, String name) {
        for(int i=0; i<voteList.size(); i++) {
            if(voteList.get(i).name.equals(name)) {
                voteList.remove(i);
                break;
            }
        }
    }

    public void addVote(Vote vote) {
        voteList.add(vote);
    }

    public void addVote(User user) {
        voteList.add(new Vote(user));
    }

    public void addVote(String name, String topic, List<VoteAnswer> answers, int votesCount, User creator) {
        voteList.add(new Vote(name, topic, answers, votesCount, creator));
    }

    public void changeName(String newName) {
        this.name = newName;
    }

    public List<Vote> getVoteList() {
        return voteList;
    }

    public String getName() {
        return name;
    }


}
