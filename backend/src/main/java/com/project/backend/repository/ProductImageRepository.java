package com.project.backend.repository;

import com.project.backend.dto.ProductDTO;
import com.project.backend.entity.ProductImages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImages,Long> {
}
