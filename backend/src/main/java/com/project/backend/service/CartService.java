package com.project.backend.service;

import com.project.backend.dto.CartDTO;
import com.project.backend.entity.Cart;
import com.project.backend.entity.Customer;
import com.project.backend.entity.Product;
import com.project.backend.repository.CartRepository;
import com.project.backend.repository.CustomerRepository;
import com.project.backend.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CartService {

    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    public CartService(CustomerRepository customerRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
    }

    public ResponseEntity<?> addToCart(CartDTO cartDTO){
        Cart cart = new Cart();

        Optional<Customer> byCustomerId = customerRepository.findById(cartDTO.getCustomerId());
        if(byCustomerId.isPresent()){
            Customer customer = byCustomerId.get();
            cart.setCustomer(customer);
        }
        else {
            return ResponseEntity.notFound().build();
        }

        Optional<Product> byProductId = productRepository.findById(cartDTO.getProductid());
        if(byProductId.isPresent()){
            Product product = byProductId.get();
            cart.setProduct(product);
        }
        else {
            return ResponseEntity.notFound().build();
        }

        cart.setQuantity(cartDTO.getQuantity());

        cartRepository.save(cart);
        return ResponseEntity.ok(cart);
    }

    public ResponseEntity<?> update(Long id,CartDTO cartDTO){
        Optional<Cart> byId = cartRepository.findById(id);
        if(byId.isPresent()){
            Cart existing = byId.get();
            existing.setQuantity(cartDTO.getQuantity());
            cartRepository.save(existing);
            return ResponseEntity.ok(existing);
        }
        else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<List<Cart>> getCartByCustomerId(Long id){
        List<Cart> byCustomerId = cartRepository.findByCustomer_Id(id);
        return ResponseEntity.ok(byCustomerId);
    }

//    public void deleteById(Long customerid){
//        cartRepository.deleteById(customerid);
//    }

    public void deleteById(Long customerid){
        cartRepository.deleteById(customerid);
    }

    @Transactional
    public void clearCart(Long customerId){
        cartRepository.deleteByCustomer_Id(customerId);
    }
}
