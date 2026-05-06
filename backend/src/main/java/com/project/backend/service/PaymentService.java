package com.project.backend.service;

import com.project.backend.dto.PaymentRequestDTO;
import com.project.backend.entity.Order;
import com.project.backend.entity.OrderItem;
import com.project.backend.entity.Payment;
import com.project.backend.entity.Product;
import com.project.backend.repository.OrderRepository;
import com.project.backend.repository.PaymentRepository;
import com.project.backend.repository.ProductRepository;
import com.stripe.model.checkout.Session;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final EmailService emailService;
    private final StripeService stripeService;

    public PaymentService(PaymentRepository paymentRepository, ProductRepository productRepository, OrderRepository orderRepository, EmailService emailService, StripeService stripeService) {
        this.paymentRepository = paymentRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.emailService = emailService;
        this.stripeService = stripeService;
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

    public Map<String,String> createStripeSession(Long orderId) throws Exception{
        Payment payment = paymentRepository.findByOrder_Id(orderId).orElseThrow(() -> new RuntimeException("Payment Not Found"));

        if(payment.getNetAmount()==null){
            throw new RuntimeException("Net Amount is missing");
        }

        double amount = payment.getNetAmount();

        Session session = stripeService.createCheckoutSession(amount,orderId);

        payment.setTransactionRef(session.getId());
        paymentRepository.save(payment);

        return Map.of("url", session.getUrl());


    }

    @Transactional
    public Object confirmPayment(String sessionId) {

        Payment payment = paymentRepository.findByTransactionRef(sessionId);

        if (payment == null) {
            throw new RuntimeException("Invalid session id");
        }

        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equals(session.getPaymentStatus())) {

                payment.setStatus("SUCCESS");
                payment.setPaymentMethod("Card");
                payment.setPaymentDate(LocalDate.now());
                payment.setPaymentTime(LocalDateTime.now());

                Order order = payment.getOrder();
                order.setStatus("CONFIRMED");

                paymentRepository.save(payment);
                orderRepository.save(order);

//                //generate pdf
//                byte[] pdf = invoiceService.generateInvoice(order);

                //  Send Email (ASYNC )
                emailService.sendMail(
                        order.getCustomer().getEmail(),
                        order.getId()
                );

                return order;

            } else {
                handleFailure(payment);
                throw new RuntimeException("Payment not completed");
            }

        } catch (Exception e) {
            handleFailure(payment);
            throw new RuntimeException("Payment verification failed");
        }
    }

    private void handleFailure(Payment payment){
        payment.setStatus("FAILED");

        Order order = payment.getOrder();
        order.setStatus("CANCELLED");

        // restore stock
        for (OrderItem item : order.getItems()) {
            Product p = productRepository.findById(item.getProductId()).get();
            p.setStock(p.getStock() + item.getQuantity());
            productRepository.save(p);
        }

        paymentRepository.save(payment);
        orderRepository.save(order);
    }
}



