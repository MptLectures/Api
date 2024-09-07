package org.example.mpt_lectures.repository;

import org.example.mpt_lectures.model.Lectures;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends ReactiveMongoRepository<Lectures, String> {}
