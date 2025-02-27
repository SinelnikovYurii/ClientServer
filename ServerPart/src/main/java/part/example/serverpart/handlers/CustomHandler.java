package part.example.serverpart.handlers;


import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;
import part.example.serverpart.business.Performer;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;
import part.example.serverpart.entity.Vote;
import part.example.serverpart.entity.VoteAnswer;
import part.example.serverpart.storage.Repository;

import java.util.List;
import java.util.regex.Pattern;


public class CustomHandler extends SimpleChannelInboundHandler {

    int votingCreationStage = 0;
    int voteStage = 0;

    Performer performer = new Performer();

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object o) throws Exception {
        String extendedViewPattern = "^extendedview \\w+$";
        String voteViewPattern = "voteview (\\S+) (\\S+)$";
        String correctVerifyPattern = "^Correct verify, \\w+$";
        String correctCreateTopicPattern = "^Correct create new topic - \\w+$";
        String wrongCreateTopicPattern = "^Topic with name - (\\w+) already created";


        String msg = (String) o;

        CommandHandler commandHandler = new CommandHandler();
        String answer = commandHandler.newUserCommand(msg);



        if(answer.equals("exit")){
            channelHandlerContext.close();
        }

        if(votingCreationStage != 0){
            votingCreationStage += performer.CreateVote(channelHandlerContext,answer);
            return;
        }

        if(voteStage != 0){
            voteStage += performer.Vote(channelHandlerContext,answer);
            return;
        }


        if(answer.equals("notverified")){

            channelHandlerContext.writeAndFlush("Access denied, please complete verification\n");

        } else if(Pattern.compile(correctVerifyPattern).matcher(answer).matches()){

            performer.newMessageToUser(channelHandlerContext,answer);

        } else if (Pattern.compile(correctCreateTopicPattern).matcher(answer).matches() || Pattern.compile(wrongCreateTopicPattern).matcher(answer).matches()) {

            performer.newMessageToUser(channelHandlerContext,answer);

        } else if(answer.equals("baseview") || Pattern.compile(voteViewPattern).matcher(answer).matches() || Pattern.compile(extendedViewPattern).matcher(answer).matches()) {

            performer.WriteView(channelHandlerContext,answer);

        }else if(answer.split(" ")[0].equals("createvote")) {

            votingCreationStage += performer.CreateVote(channelHandlerContext,answer);

        }else if(answer.split(" ")[0].equals("vote")) {

            voteStage += performer.Vote(channelHandlerContext,answer);

        }else if(answer.split(" ")[0].equals("deletevote")) {

            performer.deleteVote(channelHandlerContext,answer);

        }





    }




}
