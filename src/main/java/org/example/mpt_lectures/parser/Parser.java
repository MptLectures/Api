package org.example.mpt_lectures.parser;

import com.fasterxml.jackson.databind.JsonNode;
import org.example.mpt_lectures.model.LecturesContent;
import reactor.core.publisher.Mono;

import java.util.List;

public interface Parser {
    void startParse();

    Mono<Void> loadPage(String id) throws Exception;

    Mono<List<LecturesContent>> createListContent(JsonNode data) throws Exception;

    Mono<JsonNode> getData(String id, String type, String info);
}
