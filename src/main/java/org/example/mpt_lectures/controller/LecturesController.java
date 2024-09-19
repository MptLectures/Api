package org.example.mpt_lectures.controller;

import lombok.AllArgsConstructor;
import org.example.mpt_lectures.model.Lectures;
import org.example.mpt_lectures.service.LecturesService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1")
@AllArgsConstructor
public class LecturesController {
    
    private final LecturesService service;

    @GetMapping("/{id}")
    public Lectures getLecture(@PathVariable String id){
        return service.getLecture(id).block();
    }
}
