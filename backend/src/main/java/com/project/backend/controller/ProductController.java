package com.project.backend.controller;

import com.project.backend.dto.ProductDTO;
import com.project.backend.entity.Product;
import com.project.backend.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ProductDTO productDTO){
        return productService.create(productDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id ,@RequestBody ProductDTO productDTO){
        return productService.update(id,productDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id){
        return productService.delete(id);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll(){
        return productService.getAllProduct();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id){
        return productService.getById(id);
    }

    @GetMapping("/seller/{id}")
    public ResponseEntity<?> getBySellerId(@PathVariable Long id){
        return productService.getBySellerId(id);
    }


}
