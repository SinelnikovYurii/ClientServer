package part.example.serverpart.business;

import io.netty.channel.ChannelHandlerContext;
import org.apache.log4j.Logger;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;
import part.example.serverpart.entity.Vote;
import part.example.serverpart.entity.VoteAnswer;
import part.example.serverpart.storage.Repository;

import java.util.List;
import java.util.regex.Pattern;

public class Performer {


    String topicname;
    byte votingCreationStage = 0;
    // 1 - название
    // 2 - тема
    // 3 - кол-во ответов
    // 4 - варианты ответа
    byte voteStage = 0;

    Vote tempVote;

    String tempTopicName;
    String tempVoteName;
    String tempUserName;

    public int CreateVote(ChannelHandlerContext channelHandlerContext, String answer) {

        if(votingCreationStage == 0){
            String topicname = answer.split(" ")[1];

            List<Topic> topiclist = Repository.getTopicList();
            boolean exist = false;
            for(Topic topic : topiclist){
                if(topic.getName().equals(topicname)){
                    exist = true;
                }
            }
            if(!exist){
                channelHandlerContext.writeAndFlush("Wrong topic name, please enter a valid command ");
                return 0;
            }

            String username = answer.split(" ")[2];

            this.topicname = topicname;
            this.tempVote = new Vote(new User(username));

            votingCreationStage++;
            channelHandlerContext.writeAndFlush("Enter the name of the vote");
            return 1;
        }else if(votingCreationStage == 1){

            List<Topic> topicList = Repository.getTopicList();
            boolean exist = false;
            myBreak:
            for(Topic topic : topicList){
                List<Vote> votelist = topic.getVoteList();
                if(votelist != null){
                    for(Vote vote : votelist){
                        if(vote.getName().equals(answer)){
                            exist = true;
                            break myBreak;
                        }
                    }
                }
            }
            if(exist){
                channelHandlerContext.writeAndFlush("Vote with name " + answer + " already exists");
                return 0;
            }
            this.tempVote.setName(answer);
            votingCreationStage++;
            channelHandlerContext.writeAndFlush("Enter the description of the vote");
            return 1;
        }else if(votingCreationStage == 2){
            tempVote.setDescription(answer);
            votingCreationStage++;
            channelHandlerContext.writeAndFlush("Enter the number of answers of the vote");
            return 1;
        }else if(votingCreationStage == 3){

            try{
                int count = Integer.parseInt(answer);
                if(count >= 1){
                    tempVote.setAnswersCount(count);
                    votingCreationStage++;
                    channelHandlerContext.writeAndFlush("Enter " + 1 + " answer of the vote ");
                    return 1;
                }else{
                    channelHandlerContext.writeAndFlush("Wrong number of answers. Enter the number of answers of the vote");
                }
            }catch(NumberFormatException e){
                channelHandlerContext.writeAndFlush("Wrong number format, please enter a valid number");
            }



        }else if(votingCreationStage == 4){

            if(tempVote.getAnswers() == null){
                tempVote.addAnswer(new VoteAnswer(answer));
                if(tempVote.getAnswersCount() >= tempVote.getAnswers().size()+1){
                    channelHandlerContext.writeAndFlush("Good, " + "Enter " + (tempVote.getAnswers().size() + 1) + " answer of the vote ");
                }
            }else{
                if(tempVote.getAnswersCount() > tempVote.getAnswers().size()+1){
                    tempVote.addAnswer(new VoteAnswer(answer));
                    if(tempVote.getAnswersCount() >= tempVote.getAnswers().size()+1){
                        channelHandlerContext.writeAndFlush("Good, " + "Enter " + (tempVote.getAnswers().size() + 1) + " answer of the vote ");
                    }
                }else{
                    votingCreationStage = 0;
                    tempVote.addAnswer(new VoteAnswer(answer));
                    List<Topic> list = Repository.getTopicList();
                    for(Topic topic : list){
                        if(topic.getName().equals(topicname)){
                            topic.addVote(tempVote);
                        }
                    }
                    channelHandlerContext.writeAndFlush("New vote was created with the name " + tempVote.getName() + " in the " + topicname + " topic");
                    return -4;

                }
            }


        }

        return 0;

    }

    public boolean WriteView(ChannelHandlerContext channelHandlerContext, String msg){

        if(channelHandlerContext == null){
            return false;
        }

        String extendedViewPattern = "^extendedview \\w+$";
        String voteViewPattern = "^voteview (\\S+) (\\S+)$";

        if(msg.equals("baseview")) {

            List<Topic> temp = Repository.getTopicList();
            if(temp == null){
                return false;
            }
            for(int i = 0; i < Repository.GetTopicCount();i++){
                channelHandlerContext.writeAndFlush(temp.get(i).getName() + ": " + temp.get(i).getVoteList().size());
            }

            return true;

        }else if(Pattern.compile(extendedViewPattern).matcher(msg).matches()) {

            List<Topic> temp = Repository.getTopicList();
            if(temp == null){
                return false;
            }
            for(int i = 0; i < Repository.GetTopicCount();i++){
                if(temp.get(i).getName().equals(msg.split(" ")[1])){
                    channelHandlerContext.writeAndFlush( "votes in " + temp.get(i).getName() + "=" + temp.get(i).getVoteList().size());
                    return false;
                }
            }

        } else if (Pattern.compile(voteViewPattern).matcher(msg).matches()) {

            List<Topic> topicList = Repository.getTopicList();
            if(topicList == null){
                channelHandlerContext.writeAndFlush("No topic found");
                return false;
            }
            for(int i = 0; i < topicList.size();i++){
                if(topicList.get(i).getName().equals(msg.split(" ")[1])){

                    List<Vote> voteList = topicList.get(i).getVoteList();
                    if(voteList == null){
                        channelHandlerContext.writeAndFlush("No vote found");
                        return false;
                    }

                    for(Vote vote : voteList){

                        if(vote.getName().equals(msg.split(" ")[2])){
                            channelHandlerContext.writeAndFlush(vote.getDescription());
                            List<VoteAnswer> voteAnswers = vote.getAnswers();
                            if(voteAnswers == null){
                                return false;
                            }
                            for(VoteAnswer voteAnswer : voteAnswers){
                                channelHandlerContext.writeAndFlush(voteAnswer.getName()+ ": "+ voteAnswer.getVoteCount()+ "\n");
                            }

                        }


                    }



                }
            }




        }
        return false;
    }

    public int Vote(ChannelHandlerContext channelHandlerContext, String msg){

        if(voteStage == 0){
            String[] splitted = msg.split(" ");

            String topicName = splitted[1];
            String voteName = splitted[2];
            String userName = splitted[3];


            this.tempTopicName = topicName;
            this.tempVoteName = voteName;
            this.tempUserName = userName;

            List<Topic> temp = Repository.getTopicList();

            for(Topic topic : temp){

                if(topic.getName().equals(topicName)){
                    List<Vote> voteList = topic.getVoteList();
                    for(Vote vote : voteList){
                        if(vote.getName().equals(voteName)){

                            List<VoteAnswer> voteAnswers = vote.getAnswers();
                            for(int i = 0; i < voteAnswers.size(); i++){
                                channelHandlerContext.writeAndFlush(i+1 +": " + voteAnswers.get(i).getName() + "\n");
                            }

                            channelHandlerContext.writeAndFlush("Enter your choice =");


                        }
                    }
                }
            }
            voteStage++;
            return 1;

        }else if(voteStage == 1){

            try{
                int userVotes = Integer.parseInt(msg);

                for(Topic topic : Repository.getTopicList()){
                    if(topic.getName().equals(tempTopicName)){
                        for(Vote vote : topic.getVoteList()){
                            if(vote.getName().equals(tempVoteName)){
                                List<VoteAnswer> voteAnswers = vote.getAnswers();

                                if(userVotes > voteAnswers.size()){
                                    channelHandlerContext.writeAndFlush("Out of bounds, Enter your choice =");
                                    return 0;
                                }else{
                                    voteAnswers.get(userVotes-1).vote(new User(tempUserName));
                                    channelHandlerContext.writeAndFlush("Thank you, your opinion is important to us!");
                                    voteStage--;
                                    return -1;
                                }

                            }
                        }
                    }
                }

            } catch (NumberFormatException e) {
                channelHandlerContext.writeAndFlush("Wrong format, Enter your choice =");
            }
        }
        return 0;

    }

    public void deleteVote(ChannelHandlerContext channelHandlerContext, String msg){

        String[] splitted = msg.split(" ");
        String topicName = splitted[1];
        String voteName = splitted[2];
        String userName = splitted[3];


        List<Topic> topicList = Repository.getTopicList();
        for(Topic topic : topicList){
            if(topic.getName().equals(topicName)){
                List<Vote> voteList = topic.getVoteList();
                for(Vote vote : voteList){
                    if(vote.getName().equals(voteName)){
                        if(vote.getCreator().getName().equals(userName)){

                            for(int i = 0; i < voteList.size(); i++){
                                if(voteList.get(i).getName().equals(voteName)){
                                    voteList.remove(i);
                                    channelHandlerContext.writeAndFlush("Vote deleted");
                                    Logger.getRootLogger().info("Correct delete vote with name " + voteName);
                                    return;
                                }
                            }

                        }else{
                            channelHandlerContext.writeAndFlush("You do not have permission to delete this vote");
                            return;
                        }
                    }
                }
            }
        }

    }

    public boolean newMessageToUser(ChannelHandlerContext channelHandlerContext, String msg){
        if(channelHandlerContext != null){
            channelHandlerContext.writeAndFlush(msg);
            return true;
        }else{
            return false;
        }

    }











}
