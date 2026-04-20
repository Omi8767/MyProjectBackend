package com.project.backend.controller;

import com.project.backend.entity.Enquiry;
import com.project.backend.service.EnquiryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/enquiry")
@CrossOrigin(origins = "*")
public class EnquiryController {
    @Autowired
    private EnquiryService enquiryService;

    @PostMapping
    public ResponseEntity<Enquiry> saveData(@RequestBody Enquiry enquiry){
        Enquiry enquiry1 = enquiryService.saveData(enquiry);
        return new ResponseEntity<>(enquiry1, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Enquiry>> getData(){
        List list =enquiryService.getData();
        return new ResponseEntity<>(list,HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){

//        return new ResponseEntity<>(enquiryService.getById(id),HttpStatus.OK);
        return enquiryService.getById(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateData(@PathVariable Long id,@RequestBody Enquiry enquiry1){
        return enquiryService.updateData(id,enquiry1);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long id){
        String s = enquiryService.deleteData(id);
        return new ResponseEntity<>(s,HttpStatus.OK);
    }

}
