package com.project.backend.controller;

import com.project.backend.dto.CartDTO;
import com.project.backend.entity.Cart;
import com.project.backend.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<?> addToCart(@RequestBody CartDTO cartDTO){
        return cartService.addToCart(cartDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,@RequestBody CartDTO cartDTO){
        return cartService.update(id,cartDTO);
    }

    @GetMapping("/{customerid}")
    public ResponseEntity<List<Cart>> getCartByCustomerId(@PathVariable Long customerid){
        return cartService.getCartByCustomerId(customerid);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable Long id){
     cartService.deleteById(id);
     return  ResponseEntity.ok().build();
    }

    @DeleteMapping("/clear/customer/{id}")
    public ResponseEntity<Void> clear(@PathVariable Long id){
        cartService.clearCart(id);
        return ResponseEntity.ok().build();
    }


}
