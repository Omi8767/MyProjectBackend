package com.project.backend.repository;

import com.project.backend.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends JpaRepository<Cart,Long> {

    List<Cart> findByCustomer_Id(Long id);
    void deleteByCustomer_Id(Long id);

}
