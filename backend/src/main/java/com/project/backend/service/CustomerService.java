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
    private final EmailService emailService;
    private final OtpService otpService;

    public CustomerService(CustomerRepository customerRepository, EmailService emailService, OtpService otpService) {
        this.customerRepository = customerRepository;
        this.emailService = emailService;
        this.otpService = otpService;
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

    public void sendOtp(String email) {

        Customer user = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String otp = otpService.generateOtp(email);

        emailService.sendOtp(email, otp);
    }

    public void verifyOtp(String email, String otp) {
        boolean isValid = otpService.verifyOtp(email, otp);

        if (!isValid) {
            throw new RuntimeException("Invalid OTP");
        }
    }

    public void resetPassword(String email, String newPassword) {

        Customer user = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(newPassword); // later use encoder
        customerRepository.save(user);

        otpService.clearOtp(email); // cleanup
    }

}
