package org.example.mpt_lectures.service.Impl;

import lombok.AllArgsConstructor;
import org.example.mpt_lectures.model.Lectures;
import org.example.mpt_lectures.repository.LectureRepository;
import org.example.mpt_lectures.service.SecondSemesterService;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MongoSecondServiceImpl implements SecondSemesterService {

    private final ReactiveMongoTemplate mongoTemplate;
    private final LectureRepository repository;

    @Override
    public Mono<Lectures> getLecture(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Lectures> saveLecture(Lectures lectures) {
        return repository.save(lectures);
    }
}
