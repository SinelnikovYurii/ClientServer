package part.example.serverpart.business;


import org.apache.log4j.Logger;
import org.springframework.data.util.Pair;
import org.springframework.http.converter.json.GsonBuilderUtils;
import part.example.serverpart.enams.CommandTypes;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {

    public static Pair<CommandTypes, String> parse(String command) {


        String loginPattern = "^login -u=\\w+$";
        String createTopicPattern = "^create topic -n=\\w+$";
        String extendedViewPattern = "^view -t=\\w+$";
        String viewVotePattern1 = "^view -t=[^\\s]+ -v=[^\\s]+$";
        String viewVotePattern2 = "view -t=([^\\s]+) -v=([^\\s]+)";
        String createVotePattern = "^create vote -t=\\w+$";
        String votePattern1 = "^vote -t=[^\\s]+ -v=[^\\s]+$";
        String votePattern2 = "vote -t=([^\\s]+) -v=([^\\s]+)";
        String deleteVotePattern1 = "^delete -t=[^\\s]+ -v=[^\\s]+$";
        String deleteVotePattern2 = "^delete -t=([^\\s]+) -v=([^\\s]+)";


        if(command.equals("exit")){
            return Pair.of(CommandTypes.EXIT_USER,command);
        }

        if(Pattern.compile(loginPattern).matcher(command).matches()){
            String name = command.substring(9);

            return Pair.of(CommandTypes.LOGIN, name);
        }else if(Pattern.compile(createTopicPattern).matcher(command).matches()){
            String name = command.substring(16);

            return Pair.of(CommandTypes.CREATE_TOPIC, name);
        }else if(Pattern.compile(viewVotePattern1).matcher(command).matches()){

            Matcher matcher = Pattern.compile(viewVotePattern2).matcher(command);
            matcher.find();

            return Pair.of(CommandTypes.VOTE_VIEW, matcher.group(1) + " " + matcher.group(2));

        } else if (Pattern.compile(extendedViewPattern).matcher(command).matches()) {

            String name = command.substring(8);

            return Pair.of(CommandTypes.EXTENDED_VIEW,name);

        }else if(command.equals("view")){

            return Pair.of(CommandTypes.BASE_VIEW, "");

        }else if(Pattern.compile(createVotePattern).matcher(command).matches()){

            String name = command.substring(15);

            return Pair.of(CommandTypes.CREATE_VOTE, name);
        }else if(Pattern.compile(votePattern1).matcher(command).matches()){

            Matcher tempMatcher = Pattern.compile(votePattern2).matcher(command);
            tempMatcher.find();

            return Pair.of(CommandTypes.VOTE, tempMatcher.group(1) + " " + tempMatcher.group(2));

        }else if(Pattern.compile(deleteVotePattern1).matcher(command).matches()){

            Matcher tempMatcher = Pattern.compile(deleteVotePattern2).matcher(command);
            tempMatcher.find();

            return Pair.of(CommandTypes.DELETE_VOTE, tempMatcher.group(1) + " " + tempMatcher.group(2));

        }


        return Pair.of(CommandTypes.UNSUPPORTED, command);

    }


}
