package org.example.mpt_lectures.controller;

import lombok.AllArgsConstructor;
import org.example.mpt_lectures.model.Lectures;
import org.example.mpt_lectures.service.SecondSemesterService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("api/v1/second")
@AllArgsConstructor
public class SecondSemesterController {
    
    private final SecondSemesterService service;

    @GetMapping("/{id}")
    public Lectures getLecture(@PathVariable String id){
        return service.getLecture(id).block();
    }
    
    @PostMapping
    public void addLecture(@RequestBody Lectures lectures){
        service.saveLecture(lectures);
    }
}
