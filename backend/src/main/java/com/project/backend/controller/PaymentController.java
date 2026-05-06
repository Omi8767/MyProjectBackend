package com.project.backend.controller;

import com.project.backend.dto.PaymentRequestDTO;
import com.project.backend.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<?> makePayment(@RequestBody PaymentRequestDTO dto){
        return paymentService.savePayment(dto);
    }

    @PostMapping("/create-session/{orderId}")
    public Map<String,String> createCheckouSession(@PathVariable Long orderId) throws Exception{
        return paymentService.createStripeSession(orderId);
    }

    @GetMapping("/confirm")
    public Object confirm(@RequestParam String sessionId){
        return  paymentService.confirmPayment(sessionId);
    }
}
