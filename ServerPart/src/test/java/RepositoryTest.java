import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;
import part.example.serverpart.storage.Repository;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static part.example.serverpart.storage.Repository.getUserList;

public class RepositoryTest {


    @BeforeEach
    public void setUp() {
        for(int i = 0; i < 100; i++){
            Repository.AddTopic(new Topic("topic " + i));
        }
        for(int i = 0; i < 100; i++){
            Repository.AddUser(new User("user " + i));
        }
    }

    @AfterEach
    public void end() {
        Repository.clear();
    }

    @Test
    public void addToRepository() {

        assertEquals(100, Repository.getTopicList().size());
        assertEquals(100, getUserList().size());

    }

    @Test
    public void jsonTest() throws IOException {

        Repository.saveToJson("test1 json");
        Repository.saveToJson("test2 json");

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode json1 = objectMapper.readTree(new File("test1 json"));
        JsonNode json2 = objectMapper.readTree(new File("test2 json"));


        assertEquals(json1, json2);

    }

    @Test
    public void existTest(){

        List<User> list = Repository.getUserList();
        List<Topic> topicList = Repository.getTopicList();

        for(int i = 0; i < 100; i++){
            if(!list.get(i).getName().equals("user " + i)){
                fail();
            }
            if(!topicList.get(i).getName().equals("topic " + i)){
                fail();
            }
        }


    }




}
