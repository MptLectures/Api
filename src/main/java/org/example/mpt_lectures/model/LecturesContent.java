package org.example.mpt_lectures.model;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class LecturesContent {
    @NonNull
    private String id;
    @NonNull
    private String type;
    @NonNull
    private Object content;
    private List<LecturesContent> children;
}
