package part.example.userpart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class UserPartApplication {

    static ApplicationContext applicationContext;

    public static void main(String[] args) {
        SpringApplication.run(UserPartApplication.class, args);



    }

}
