package org.example.mpt_lectures.model;

import lombok.Data;
import lombok.NonNull;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "lectures")
@TypeAlias("Lectures")
public class Lectures {
    @NonNull
    private String id;
    @NonNull
    private List<LecturesContent> lecturesContents;

    public @NonNull String getId() {
        return id;
    }
}
