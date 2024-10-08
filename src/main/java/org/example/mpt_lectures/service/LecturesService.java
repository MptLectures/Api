package org.example.mpt_lectures.service;

import org.example.mpt_lectures.model.Lectures;
import reactor.core.publisher.Mono;

public interface LecturesService {
    Mono<Lectures> getLecture(String id);
    
    Mono<Lectures> saveLecture(Lectures lectures);
}
