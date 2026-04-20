package com.project.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "category_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler","products"})
    private Category category;

    private String name;
    private Double price;
    private String unit;
    private Integer stock;

    private boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    @JsonIgnoreProperties({"password,address,contact"})
    private Customer seller;

    private LocalDateTime createdAt;

    public Product() {
        this.createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<ProductImages> images = new ArrayList<>();

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonManagedReference
    private List<ProductSpecification> specifications = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Customer getSeller() {
        return seller;
    }

    public void setSeller(Customer seller) {
        this.seller = seller;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<ProductImages> getImages() {
        return images;
    }

    public void setImages(List<ProductImages> images) {
        this.images = images;
    }

    public List<ProductSpecification> getSpecifications() {
        return specifications;
    }

    public void setSpecifications(List<ProductSpecification> specifications) {
        this.specifications = specifications;
    }
}
