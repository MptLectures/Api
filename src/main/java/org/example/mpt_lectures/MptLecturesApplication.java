package org.example.mpt_lectures;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.example.mpt_lectures.parser.Impl.SecondSemesterParser;
import org.example.mpt_lectures.parser.Parser;
import org.example.mpt_lectures.service.SecondSemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication
public class MptLecturesApplication {
    
    private Parser parser;

    @Autowired
    public MptLecturesApplication(Parser parser) {
        this.parser = parser;
    }

    public static void main(String[] args) {
        SpringApplication.run(MptLecturesApplication.class, args);
    }
    
    @PostConstruct
    public void init() {
        parser.startParse();
    }
}
