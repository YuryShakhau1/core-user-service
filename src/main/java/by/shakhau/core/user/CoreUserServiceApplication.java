package by.shakhau.core.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CoreUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreUserServiceApplication.class, args);
    }
}
