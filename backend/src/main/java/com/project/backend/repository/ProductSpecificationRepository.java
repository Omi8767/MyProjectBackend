package com.project.backend.repository;

//import com.project.backend.dto.SpecificationDTO;
import com.project.backend.entity.ProductSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSpecificationRepository extends JpaRepository<ProductSpecification,Long> {

    List<ProductSpecification> findByProduct_Id(Long productId);

}
