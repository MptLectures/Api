package org.example.mpt_lectures.parser.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mpt_lectures.model.Lectures;
import org.example.mpt_lectures.model.LecturesContent;
import org.example.mpt_lectures.parser.Parser;
import org.example.mpt_lectures.service.SecondSemesterService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Service
public class SecondSemesterParser implements Parser {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    
    private final WebClient webClient;
    private final SecondSemesterService service;

    public SecondSemesterParser(SecondSemesterService service, WebClient.Builder webClientBuilder, @Value("${notion.api.token}") String TOKEN) {
        this.service = service;
        this.webClient = webClientBuilder
                .baseUrl("https://api.notion.com/v1/")
                .defaultHeader("Authorization", "Bearer " + TOKEN)
                .defaultHeader("Notion-Version", "2022-06-28")
                .build();
    }

    public void startParse() {
        loadPage("928228ff6e9844a59f20445dae52401d")
                .doOnSuccess(aVoid -> System.out.println("Parsing completed successfully"))
                .doOnError(e -> System.err.println("Parsing failed: " + e.getMessage()))
                .subscribe();
    }


    public Mono<Void> loadPage(String id) {
        return getData(id, "blocks", "/children")
                .flatMap(data -> {
                    return getData(id, "pages", "")
                            .flatMap(dataPage -> {
                                Object header = extractHeader(dataPage);
                                List<LecturesContent> processedContent = new ArrayList<>();
                                if (header != null) {
                                    processedContent.add(new LecturesContent(id, "header", header));
                                }
                                Object icon = extractIcon(dataPage);
                                if (icon != null) {
                                    processedContent.add(new LecturesContent(id, "icon", icon));
                                }
                                
                                System.out.println(processedContent);

                                return createListContent(data.path("results"))
                                        .map(children -> {
                                            processedContent.addAll(children);
                                            System.out.println(processedContent);
                                            return new Lectures(id, processedContent);
                                        })
                                        .flatMap(lecture -> {
                                            return service.saveLecture(lecture).thenReturn(lecture); 
                                        })
                                        .then()  
                                        .doOnError(e -> System.err.println("Error in processing: " + e.getMessage()));
                            });
                })
                .onErrorResume(e -> {
                    System.err.println("Error in loadPage: " + e.getMessage());
                    return Mono.empty();
                });
    }




    public Mono<JsonNode> getData(String id, String type, String info) {
        String url = String.format("%s/%s%s", type, id, info);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        return OBJECT_MAPPER.readTree(body);
                    } catch (Exception e) {
                        System.out.println("Error parsing response: " + e.getMessage());
                        throw new RuntimeException("Failed to parse response", e);
                    }
                })
                .doOnError(e -> System.out.println("Error during request: " + e.getMessage()));
    }
    
    public Mono<JsonNode> postData(String id, String type, String info) {
        String url = String.format("%s/%s%s", type, id, info);
        return webClient.post()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnNext(response -> System.out.println("Response received: " + response))
                .map(body -> {
                    try {
                        return OBJECT_MAPPER.readTree(String.valueOf(body));
                    } catch (Exception e) {
                        System.out.println("Error parsing response: " + e.getMessage());
                        throw new RuntimeException("Failed to parse response", e);
                    }
                })
                .doOnError(e -> System.out.println("Error during request: " + e.getMessage()));
    }

    private Object extractHeader(JsonNode dataPage) {
        try {
            if (dataPage.has("properties")) {
                JsonNode nameNode = dataPage.path("properties").path("Name");
                if (nameNode.has("title")) {
                    return OBJECT_MAPPER.treeToValue(nameNode.path("title"), Object.class);
                }
                JsonNode titleNode = dataPage.path("properties").path("title");
                if (titleNode.has("title")) {
                    return OBJECT_MAPPER.treeToValue(titleNode.path("title"), Object.class);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract header", e);
        }
        return null;
    }

    private Object extractIcon(JsonNode dataPage) {
        try {
            if (dataPage.has("icon")) {
                String iconType = dataPage.path("icon").path("type").asText();
                return OBJECT_MAPPER.treeToValue(dataPage.path("icon").path(iconType), Object.class);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract icon", e);
        }
        return null;
    }

    public Mono<List<LecturesContent>> createListContent(JsonNode data) {
        List<Mono<LecturesContent>> contentMonos = new ArrayList<>();
        for (JsonNode element : data) {
            String type = element.path("type").asText();
            String id = element.path("id").asText();
            
            Mono<LecturesContent> contentMono = Mono.fromCallable(() -> {
                Object contentObj = OBJECT_MAPPER.treeToValue(element.path(type), Object.class);
                LecturesContent itemContent = new LecturesContent(id, type, contentObj);
                return itemContent;
            }).flatMap(itemContent -> {
                if (element.path("has_children").asBoolean() && !"child_page".equals(type)) {
                    return getData(id, "blocks", "/children")
                            .flatMap(response -> createListContent(response.path("results")))
                            .map(children -> {
                                itemContent.setChildren(children);
                                return itemContent;
                            });
                } else if ("child_page".equals(type)) {
                    return loadPage(id).thenReturn(itemContent);
                } else if ("child_database".equals(type)) {
                    return postData(id, "databases", "/query")
                            .flatMap(response -> createListDatabase(response.path("results")))
                            .map(children -> {
                                itemContent.setChildren(children);
                                return itemContent;
                            });
                }
                return Mono.just(itemContent);
            });

            contentMonos.add(contentMono);
        }

        return Mono.zip(contentMonos, objects -> {
            List<LecturesContent> contents = new ArrayList<>();
            for (Object object : objects) {
                contents.add((LecturesContent) object);
            }
            return contents;
        });
    }
    
    private Mono<List<LecturesContent>> createListDatabase(JsonNode data) {
        List<Mono<LecturesContent>> contentMonos = new ArrayList<>();
        for (JsonNode element : data) {
            String type = "child_page";
            String id = element.path("id").asText();
            Mono<LecturesContent> contentMono = Mono.fromCallable(() -> {
                Object header = extractHeader(element);
                LecturesContent itemContent = new LecturesContent(id, type, header);
                return itemContent;
            }).flatMap(itemContent -> {
                return loadPage(itemContent.getId()).thenReturn(itemContent);
            });

            contentMonos.add(contentMono);
        }
        
        return Mono.zip(contentMonos, objects -> {
            List<LecturesContent> contents = new ArrayList<>();
            for (Object object : objects) {
                contents.add((LecturesContent) object);
            }
            return contents;
        });
    }
}
