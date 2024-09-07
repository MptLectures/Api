package org.example.mpt_lectures.repository;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Configuration
public class SecondConfMongo {

    @Primary
    @Bean(name = "secondMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.second")
    public MongoProperties getMongoProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "secondMongoClient")
    public MongoClient mongoClient(@Qualifier("secondMongoProperties") MongoProperties mongoProperties) {
        return MongoClients.create(mongoProperties.getUri());
    }

    @Primary
    @Bean(name = "reactiveMongoTemplate")
    public ReactiveMongoTemplate reactiveMongoTemplate(@Qualifier("secondMongoClient") MongoClient mongoClient,
                                                       @Qualifier("secondMongoProperties") MongoProperties mongoProperties) {
        return new ReactiveMongoTemplate(mongoClient, mongoProperties.getDatabase());
    }
}
