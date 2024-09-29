package org.example.t100;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class T100Application {

    public static void main(String[] args) {
        SpringApplication.run(T100Application.class, args);
    }

}
