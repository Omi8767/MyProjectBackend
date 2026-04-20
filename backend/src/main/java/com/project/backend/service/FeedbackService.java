package com.project.backend.service;

import com.project.backend.entity.Feedback;
import com.project.backend.repository.FeedbackRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public ResponseEntity<?> create(Feedback feedback){

            Feedback save = feedbackRepository.save(feedback);
            return new ResponseEntity<>(save, HttpStatus.CREATED);

    }
    public ResponseEntity<List<Feedback>> getAll(){
        List<Feedback> all = feedbackRepository.findAll();
        return new ResponseEntity<>(all,HttpStatus.OK);
    }

    public ResponseEntity<?> getById(Long id){
        Optional<Feedback> byId = feedbackRepository.findById(id);
        if(byId.isPresent()){
            Optional<Feedback> feedback1 = feedbackRepository.findById(id);
            return new ResponseEntity<>(feedback1,HttpStatus.OK);
        }
        return new ResponseEntity<>("Feedback Not Found...",HttpStatus.NOT_FOUND);
    }
}
