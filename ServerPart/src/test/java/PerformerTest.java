import io.netty.channel.ChannelHandlerContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import part.example.serverpart.business.Performer;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;
import part.example.serverpart.entity.Vote;
import part.example.serverpart.entity.VoteAnswer;
import part.example.serverpart.storage.Repository;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class PerformerTest {


    private ChannelHandlerContext channelHandlerContext;
    private Performer performer;

    @BeforeEach
    public void setUp() {
        channelHandlerContext = mock(ChannelHandlerContext.class);
        performer = new Performer();
    }

    @Test
    public void sendFakeChannel() {

        Performer performer = new Performer();
        boolean success = performer.newMessageToUser(null, "test");
        assertFalse(success);

    }

    @Test
    public void sendFakeChannel2() {

        Performer performer = new Performer();
        boolean success = performer.WriteView(null, "baseview");
        assertFalse(success);

    }

    @Test
    public void testWriteView() {

        List<Topic> topics = new ArrayList<>();
        topics.add(new Topic("Topic1", new ArrayList<>()));
        topics.add(new Topic("Topic2", new ArrayList<>()));
        Repository.setTopicList(topics);

        boolean result = performer.WriteView(channelHandlerContext, "baseview");

        assertTrue(result);
        verify(channelHandlerContext, times(2)).writeAndFlush(anyString());
    }

    @Test
    public void testCreateVote() {

        Repository.AddTopic(new Topic("Topic"));
        Repository.AddUser(new User("User"));

        int result = performer.CreateVote(channelHandlerContext, "createvote Topic User");
        assertEquals(1, result);

        result = performer.CreateVote(channelHandlerContext, "VoteName");
        assertEquals(1, result);

        result = performer.CreateVote(channelHandlerContext, "Description");
        assertEquals(1, result);

        result = performer.CreateVote(channelHandlerContext, "2");
        assertEquals(1, result);

        result = performer.CreateVote(channelHandlerContext, "Answer1");
        assertEquals(0, result);

        result = performer.CreateVote(channelHandlerContext, "Answer2");
        assertEquals(-4, result);
        verify(channelHandlerContext, times(6)).writeAndFlush(anyString());
    }

    @Test
    public void testVote() {

        Topic topic = new Topic("TopicName");
        User user = new User("User");
        Vote vote = new Vote(user);
        vote.setName("VoteName");
        vote.setDescription("Description");
        vote.addAnswer(new VoteAnswer("Answer1"));
        vote.addAnswer(new VoteAnswer("Answer2"));
        topic.addVote(vote);
        Repository.AddTopic(topic);
        performer.Vote(channelHandlerContext, "vote TopicName VoteName User");

        int result = performer.Vote(channelHandlerContext, "1");

        assertEquals(-1, result);
        verify(channelHandlerContext).writeAndFlush("Thank you, your opinion is important to us!");
    }

    @Test
    public void testDeleteVote() {

        Topic topic = new Topic("TopicName");
        User user = new User("User");
        Vote vote = new Vote(user);
        vote.setName("VoteName");
        vote.setDescription("Description");
        topic.addVote(vote);
        Repository.AddTopic(topic);

        performer.deleteVote(channelHandlerContext, "deletevote TopicName VoteName User");

        verify(channelHandlerContext).writeAndFlush("Vote deleted");
        assertTrue(topic.getVoteList().isEmpty());
    }





}
