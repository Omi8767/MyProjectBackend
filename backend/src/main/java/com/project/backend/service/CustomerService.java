package com.project.backend.service;

import com.project.backend.dto.LoginRequest;
import com.project.backend.entity.Customer;
import com.project.backend.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public ResponseEntity<?> registration(Customer customer){
        Optional<Customer> byEmail = customerRepository.findByEmail(customer.getEmail());
        if(byEmail.isPresent()){
            // return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
            return  ResponseEntity.badRequest().body("Email already exists");
        }

        Customer save = customerRepository.save(customer);
        save.setPassword(null);
        return ResponseEntity.ok(save);
    }

    public ResponseEntity<?> login(LoginRequest request){
        Optional<Customer> byEmail = customerRepository.findByEmail(request.getEmail());
        if(byEmail.isPresent()){
            Customer customer = byEmail.get();
            if(customer.getPassword().equals(request.getPassword())){
                customer.setPassword(null); // hide password
                return ResponseEntity.ok(customer);
            }
            return ResponseEntity.status(401).body("Invalid username password");
        }
        return ResponseEntity.status(401).body("Invalid email or password");
    }
}
