package part.example.serverpart.handlers;
import org.apache.log4j.Logger;
import org.springframework.data.util.Pair;
import part.example.serverpart.business.CommandParser;
import part.example.serverpart.business.Performer;
import part.example.serverpart.enams.CommandTypes;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;
import part.example.serverpart.storage.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler {


    static User currentUser;


    public String newUserCommand(String command) {

        Pair<CommandTypes,String> temp = CommandParser.parse(command);

        if(temp.getFirst() == CommandTypes.EXIT_USER){
            return temp.getSecond();
        }

        if(temp.getFirst() == CommandTypes.LOGIN) {
            if(Login(temp.getSecond())){
                Logger.getRootLogger().info("New user with name " + temp.getSecond());
                Logger.getRootLogger().info("Correct verify " + temp.getSecond() + ", is logged in");
            }else{
                Logger.getRootLogger().info("Correct verify " + temp.getSecond() + ", is logged in");
            }

            return "Correct verify, "+ temp.getSecond();
        }
        if(currentUser != null) {
            if(temp.getFirst() == CommandTypes.CREATE_TOPIC){
                if(CreateTopic(temp.getSecond())){
                    Logger.getRootLogger().info("Correct create new topic - " + temp.getSecond());
                    return "Correct create new topic - " + temp.getSecond();
                }else{
                    Logger.getRootLogger().info("Topic with name - " + temp.getSecond() + " already created");
                    return "Topic with name - " + temp.getSecond() + " already created";
                }


            } else if (temp.getFirst() == CommandTypes.BASE_VIEW) {
                Logger.getRootLogger().info(currentUser.getName() + " baseview");

                return "baseview";
            } else if(temp.getFirst() == CommandTypes.EXTENDED_VIEW){
                Logger.getRootLogger().info(currentUser.getName() + " extendedview");

                return "extendedview " + temp.getSecond();
            }else if(temp.getFirst() == CommandTypes.VOTE_VIEW){
                Logger.getRootLogger().info(currentUser.getName() + " voteview");

                return "voteview " + temp.getSecond();
            }else if(temp.getFirst() == CommandTypes.CREATE_VOTE){

                Logger.getRootLogger().info(currentUser.getName() + " createvote");

                return "createvote " + temp.getSecond() + " " + currentUser.getName();
            }else if(temp.getFirst() == CommandTypes.VOTE){
                Logger.getRootLogger().info(currentUser.getName() + " vote");

                return "vote " + temp.getSecond() + " " + currentUser.getName();
            }else if(temp.getFirst() == CommandTypes.DELETE_VOTE){
                Logger.getRootLogger().info(currentUser.getName() + " deletevote");

                return "deletevote " + temp.getSecond() + " " + currentUser.getName();
            }

        }else{
            return "notverified";
        }


        return command;
    }

    public boolean isLoggedIn(){
        return currentUser != null;
    }

    private boolean CreateTopic(String name) {
        Topic topic = Repository.TopicExists(name);
        if(topic == null) {
            Repository.AddTopic(new Topic(name));

            return true;
        }
        return false;

    }

    private boolean Login(String username) {

        User temp = Repository.UserExists(username);
        if(temp != null) {
            currentUser = temp;
            return false;
        }else{
            User user = new User(username);
            Repository.AddUser(user);
            currentUser = user;
            return true;
        }


    }







}
