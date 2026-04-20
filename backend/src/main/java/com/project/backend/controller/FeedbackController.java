package com.project.backend.controller;

import com.project.backend.entity.Feedback;
import com.project.backend.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<?> save(@RequestBody Feedback feedback){
        return feedbackService.create(feedback);
    }

    @GetMapping
    public ResponseEntity<List<Feedback>> getAll(){
        return   feedbackService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return feedbackService.getById(id);
    }


}
