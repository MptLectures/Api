package org.example.mpt_lectures;

import jakarta.annotation.PostConstruct;
import org.example.mpt_lectures.parser.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MptLecturesApplication {

    public static void main(String[] args) {
        SpringApplication.run(MptLecturesApplication.class, args);
    }
}
