package com.project.backend.service;

import com.project.backend.dto.OrderDTO;
import com.project.backend.dto.OrderItemDTO;
import com.project.backend.entity.*;
import com.project.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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

    public Map<String, Object> getOrderBySellerId(Long sellerId){
        List<Order> orders = orderRepository.findByItemsSellerId(sellerId);

        List<Payment> payments = orders.stream()
                .map(Order::getPayment)
                .filter(p -> p != null)
                .toList();
        Map<String,Object> data = new HashMap<>();

        //Total Orders
        data.put("totalOrders",orders.size());

        //Total Customers
        long totalCustomers = orders.stream()
                .map(o -> o.getCustomer()).distinct().count();

        data.put("totalCustomers",totalCustomers);

        //Total Revenue

        double revenue =payments.stream()
                .filter(p->"SUCCESS".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getNetAmount)
                .sum();

        data.put("totalRevenue",revenue);

//        PENDING BOOKINGS

        long pendingOrders = orders.stream()
                .filter(o -> "PENDING".equalsIgnoreCase(o.getStatus()))
                .count();

        data.put("pendingOrders", pendingOrders);

        // STATUS COUNTS

        data.put("confirmed", orders.stream()
                .filter(o -> "CONFIRMED".equalsIgnoreCase(o.getStatus()))
                .count());

        data.put("inProcess", orders.stream()
                .filter(o -> "IN_PROCESS".equalsIgnoreCase(o.getStatus()))
                .count());

        data.put("dispatch", orders.stream()
                .filter(o -> "DISPATCH".equalsIgnoreCase(o.getStatus()))
                .count());

        data.put("delivered", orders.stream()
                .filter(o -> "DELIVERED".equalsIgnoreCase(o.getStatus()))
                .count());

        data.put("rejected", orders.stream()
                .filter(o -> "REJECT".equalsIgnoreCase(o.getStatus()))
                .count());

        data.put("cancelled", orders.stream()
                .filter(o -> "CANCELLED".equalsIgnoreCase(o.getStatus()))
                .count());

        // =========================
        // PAYMENT STATUS
        // =========================
        data.put("paid", payments.stream()
                .filter(p -> "SUCCESS".equalsIgnoreCase(p.getStatus()))
                .count());

        data.put("pendingPayments", payments.stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .count());

        data.put("refundPayments", payments.stream()
                .filter(p -> "REFUNDED".equalsIgnoreCase(p.getStatus()))
                .count());

        return  data;
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
                payment.setStatus("REFUNDED");
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

    public ResponseEntity<List<Order>> getAllOrders(){
        List<Order> orders = orderRepository.findAll();
        return ResponseEntity.ok(orders);
    }

    public ResponseEntity<?> updateStatus(Long id,String status){
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order Not Found"));
        Payment payment = order.getPayment();

        if(status.equalsIgnoreCase("Delivered")&&payment.getPaymentMethod().equalsIgnoreCase("COD")){
            order.setStatus(status);
            payment.setStatus("SUCCESS");
            paymentRepository.save(payment);
        }
        order.setStatus(status);
        Order save = orderRepository.save(order);
        return ResponseEntity.ok(save);
    }

    public List<String> getAllCities(){
        return orderRepository.findDistinctCities();
    }

    public List<Order> filters(String fromDate,String toDate,String city){
        LocalDateTime from = null;
        LocalDateTime  to = null;

        if(fromDate != null && !fromDate.isBlank()){
            from = LocalDate.parse(fromDate).atStartOfDay();
        }

        if(toDate != null && !toDate.isBlank()){
            to = LocalDate.parse(toDate).atTime(23,59,59);
        }

        boolean hasDate = from !=null && to !=null;
        boolean hasCity = city != null&& !city.isBlank();

        if(hasDate && hasCity){
            return  orderRepository.findByOrderDateBetweenAndShippingCityIgnoreCase(from,to,city);
        }

        if(hasDate){
            return orderRepository.findByOrderDateBetween(from,to);
        }
        if(hasCity){
            return orderRepository.findByShippingCityIgnoreCase(city);
        }

        return  orderRepository.findAll();
    }



    public Map<String, Object> getDashboard(){
        List<Order> orders = orderRepository.findAll();
        List<Customer> customers = customerRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();

        Map<String,Object> data = new HashMap<>();

        data.put("totalOrders",orders.size());

        data.put("totalCustomers",customers.size());

        double revenue= payments.stream()
                .filter(p->"SUCCESS".equals(p.getStatus()))
                .mapToDouble(Payment::getNetAmount)
                .sum();

        data.put("totalRevenue",revenue);

        long pendingOrders = orders.stream()
                .filter(o->o.getStatus().equals("IN_PROCESS"))
                .count();

        data.put("pendingOrders",pendingOrders);

        data.put("inProcess",orders.stream()
                .filter(o->o.getStatus().equals("IN_PROCESS"))
                .count());

        data.put("confirm",orders.stream()
                .filter(o->o.getStatus().equals("CONFIRMED"))
                .count());

        data.put("dispatch",orders.stream()
                .filter(o->o.getStatus().equals("DISPATCH"))
                .count());

        data.put("delivered",orders.stream()
                .filter(o->o.getStatus().equals("DELIVERED"))
                .count());


        data.put("rejected",orders.stream()
                .filter(o->o.getStatus().equals("REJECT"))
                .count());

        data.put("cancelled",orders.stream()
                .filter(o->o.getStatus().equals("CANCELLED"))
                .count());

        data.put("paid",payments.stream()
                .filter(p->p.getStatus().equals("SUCCESS"))
                .count());

        data.put("pendingPayments",payments.stream()
                .filter(p->p.getStatus().equals("PENDING"))
                .count());

        data.put("refundPayments",payments.stream()
                .filter(p->p.getStatus().equals("REFUNDED"))
                .count());

        return data;

    }
}

