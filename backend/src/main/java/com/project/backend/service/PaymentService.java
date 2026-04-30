package com.project.backend.service;

import com.project.backend.dto.PaymentRequestDTO;
import com.project.backend.entity.Order;
import com.project.backend.entity.Payment;
import com.project.backend.repository.OrderRepository;
import com.project.backend.repository.PaymentRepository;
import com.project.backend.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;

    public PaymentService(PaymentRepository paymentRepository, ProductRepository productRepository, OrderRepository orderRepository, EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
    }

    @Transactional
    public ResponseEntity<?> savePayment(PaymentRequestDTO dto){
        Payment payment = null;
        Optional<Payment> byOrderId = paymentRepository.findByOrder_Id(dto.getOrderId());
        if(byOrderId.isPresent()){
            payment = byOrderId.get();
        }
        else {
            return new ResponseEntity<>("Payment not found", HttpStatus.NOT_FOUND);
        }

        payment.setPaymentMethod(dto.getPaymentMethod());
        payment.setTransactionRef(dto.getTransactionRef());
        payment.setStatus("SUCCESS");
        payment.setPaymentTime(LocalDateTime.now());

        payment.setNetAmount(dto.getNetAmount());

        Order order = payment.getOrder();
        order.setStatus("CONFIRMED");

        Order save = orderRepository.save(order);

        emailService.sendMail(order.getCustomer().getEmail(),order.getId());

        Payment save1 = paymentRepository.save(payment);
        return ResponseEntity.ok(save1);
    }
}

