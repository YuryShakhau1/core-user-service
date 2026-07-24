package by.shakhau.core.user;

import lombok.AllArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@AllArgsConstructor
@SpringBootApplication
@EnableJpaAuditing
public class CoreUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreUserServiceApplication.class, args);
    }
}
