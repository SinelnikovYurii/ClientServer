package part.example.serverpart.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import part.example.serverpart.entity.Topic;
import part.example.serverpart.entity.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Repository {

    static List<Topic> topicList = new ArrayList<>();
    static List<User> userList = new ArrayList<>();

    public static void setTopicList(List<Topic> topicList) {
        Repository.topicList = topicList;
    }

    public static void setUserList(List<User> userList) {
        Repository.userList = userList;
    }

    public static void AddUser(User user) {
        userList.add(user);
    }
    public static void AddTopic(Topic topic){
        topicList.add(topic);
    }

    public Repository(){

    }

    public static User UserExists(String username) {
        for(User user : userList){
            if(user.getName().equals(username)){
                return user;
            }
        }
        return null;
    }

    public static Topic TopicExists(String topicName) {
        for(Topic topic : topicList){
            if(topic.getName().equals(topicName)){
                return topic;
            }
        }
        return null;
    }

    public static int GetTopicCount() {
        return topicList.size();
    }

    public static List<Topic> getTopicList() {
        return topicList;
    }
    public static List<User> getUserList() {
        return userList;
    }

    public static void saveToJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RepositoryData data = new RepositoryData(userList, topicList);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), data);
            System.out.println("Данные успешно сохранены в " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromJson(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            RepositoryData data = objectMapper.readValue(new File(filePath), RepositoryData.class);
            userList.clear();
            topicList.clear();

            if (data.users != null) {
                userList.addAll(data.users);
            }
            if (data.topics != null) {
                topicList.addAll(data.topics);
            }
            System.out.println("Данные успешно загружены из " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clear() {
        userList.clear();
        topicList.clear();
    }


    private static class RepositoryData {
        public List<User> users;
        public List<Topic> topics;

        public RepositoryData(){
            users = new ArrayList<>();
            topics = new ArrayList<>();
        }

        public RepositoryData(List<User> users, List<Topic> topics) {
            this.users = users;
            this.topics = topics;
        }
    }





}
