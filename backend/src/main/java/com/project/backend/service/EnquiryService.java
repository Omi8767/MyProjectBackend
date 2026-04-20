package com.project.backend.service;

import com.project.backend.entity.Enquiry;
import com.project.backend.repository.EnquiryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnquiryService {

    @Autowired
    private EnquiryRepository enquiryRepository;

    public Enquiry saveData(Enquiry enquiry){
        return enquiryRepository.save(enquiry);
    }

    public List<Enquiry> getData(){
        return enquiryRepository.findAll();
    }

    public ResponseEntity<?> getById(Long id){
        Optional<Enquiry> byId = enquiryRepository.findById(id);
        if(byId.isPresent()){
            return new ResponseEntity<>(enquiryRepository.findById(id),HttpStatus.OK);
        }
        return new ResponseEntity<>("record not found...",HttpStatus.CONFLICT);
    }

    public ResponseEntity<?> updateData(Long id, Enquiry enquiry1){
        Optional<Enquiry> byId = enquiryRepository.findById(id);
        if(byId.isPresent()){
            Enquiry enquiry = byId.get();
            enquiry.setName(enquiry1.getName());
            enquiry.setEmail(enquiry1.getEmail());
            enquiry.setContact(enquiry1.getContact());
            enquiry.setSubject(enquiry1.getSubject());
            enquiry.setMessage(enquiry1.getMessage());

            Enquiry save = enquiryRepository.save(enquiry);
            return new ResponseEntity<>(save, HttpStatus.OK);
        }
        return new ResponseEntity<>("User Not Found",HttpStatus.NOT_FOUND);
    }

    public String deleteData(Long id){
        Optional<Enquiry> byId = enquiryRepository.findById(id);
        if (byId.isPresent()){
//            Enquiry1 enquiry1 = byId.get();
            enquiryRepository.deleteById(id);
            return "Record deleted Successfully";
        }
        return "User Not Found";

    }
}
