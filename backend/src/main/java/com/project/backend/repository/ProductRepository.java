package com.project.backend.repository;

import com.project.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product,Long> {
     List<Product> findBySeller_Id(Long id);
}
