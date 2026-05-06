package com.project.backend.service;

import com.project.backend.dto.OrderDTO;
import com.project.backend.dto.OrderItemDTO;
import com.project.backend.entity.*;
import com.project.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Service
public class OrderService {

    private final CustomerRepository customerRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final StripeService stripeService;
    private final EmailService emailService;

    public OrderService(CustomerRepository customerRepository, CartRepository cartRepository, ProductRepository productRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, StripeService stripeService, EmailService emailService) {
        this.customerRepository = customerRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.stripeService = stripeService;
        this.emailService = emailService;
    }

    public ResponseEntity<?> placeOrder(OrderDTO orderDTO){
        Order order = new Order();
        Optional<Customer> byId = customerRepository.findById(orderDTO.getCustomerId());
        if(byId.isPresent()){
            Customer customer = byId.get();
            order.setCustomer(customer);
        }
        else{
            return  new ResponseEntity<>("Customer Not Found", HttpStatus.NOT_FOUND);
        }

        List<OrderItem> orderItems = new ArrayList<>();
        double total=0.0;
        for(OrderItemDTO itm : orderDTO.getItems()){
            Product product = productRepository.findById(itm.getProductId()).get();

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setQuantity(itm.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItem.setTotal(product.getPrice()*itm.getQuantity());
            orderItem.setSellerId(product.getSeller().getId());
            orderItem.setImageUrl(product.getImages().get(0).getImageUrl());

            orderItems.add(orderItem);

            product.setStock(product.getStock()- itm.getQuantity());
            productRepository.save(product);

            total+=orderItem.getTotal();
        }

        order.setStatus("IN_PROCESS");
        order.setTotalAmount(total);

        ShippingInfo shippingInfo = new ShippingInfo();
        if(orderDTO.getShipping() != null){
            shippingInfo.setName(orderDTO.getShipping().getName());
            shippingInfo.setAddress(orderDTO.getShipping().getAddress());
            shippingInfo.setCity(orderDTO.getShipping().getCity());
            shippingInfo.setPincode(orderDTO.getShipping().getPincode());
            shippingInfo.setContact(orderDTO.getShipping().getContact());
        }

        order.setShipping(shippingInfo);
        order.setItems(orderItems);

        Order saveOrder = orderRepository.save(order);


        //Payment
        Payment payment = new Payment();
        payment.setOrder(saveOrder);
        payment.setTotalAmount(total);

        double gstPercent = orderDTO.getGstPercent() != null ? orderDTO.getGstPercent() : 0;
        double discountPercent = orderDTO.getDiscountPercent() != null ? orderDTO.getDiscountPercent() : 0;

        double gst = total * gstPercent /100;
        double discount = total* discountPercent /100;

        double netAmt = total + gst - discount;

        payment.setNetAmount(netAmt);

        payment.setGstPercent(gstPercent);
        payment.setDiscountPercent(discountPercent);
        payment.setGstAmount(gst);
        payment.setDiscountAmount(discount);

        payment.setStatus("PENDING");

        paymentRepository.save(payment);

        return new ResponseEntity<>(saveOrder,HttpStatus.CREATED);
    }

    public ResponseEntity<?> getOrderByCustomerId(Long customerId){
        List<Order> byCustomerId = orderRepository.findByCustomer_Id(customerId);
        return  new ResponseEntity<>(byCustomerId,HttpStatus.OK);
    }

    @Transactional
    public Order cancleOrder(Long orderId){
        Order order = orderRepository.findById(orderId).orElseThrow(()-> new RuntimeException("Order Not Found"));

        if(order.getStatus().equals("DISPATCH") || order.getStatus().equals("DELIVERED")){
            throw new RuntimeException("Order cannot be cancelled");
        }

        Payment payment=paymentRepository.findByOrder_Id(orderId).orElse(null);
        for(OrderItem item:order.getItems()){
            Product p = productRepository.findById(item.getProductId()).orElseThrow();
            p.setStock(p.getStock()+ item.getQuantity());

            productRepository.save(p);
        }
        String email = order.getCustomer().getEmail();
        String subject;
        String body;

        if(payment != null && "SUCCESS".equals(payment.getStatus()) && "Card".equalsIgnoreCase(payment.getPaymentMethod())){
            try {
                stripeService.refundPayment(payment.getTransactionRef());
                payment.setStatus("RRFUNDED");
                paymentRepository.save(payment);
            }catch(Exception e){
                throw  new RuntimeException("Refund Failed");
            }

            subject="Order Cancelled and Refund Initiated";
            body="Hello \" + order.getCustomer().getName() + \",\\n\\n\" +\n" +
                    "                            \"Your order #\" + order.getId() + \" has been cancelled.\\n\\n\" +\n" +
                    "                            \" Since you paid via Card, your refund has been initiated.\\n\" +\n" +
                    "                            \"It will be credited within 2-3 business days.\\n\\n\" +\n" +
                    "                            \"Thank you for shopping with us.";
            emailService.sendSimpleEmail(email,subject,body);
        }
        order.setStatus("CANCELLED");
        return orderRepository.save(order);
    }

}
