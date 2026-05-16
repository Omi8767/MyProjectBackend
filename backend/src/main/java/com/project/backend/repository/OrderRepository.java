package com.project.backend.repository;

import com.project.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCustomer_Id(Long id);


    List<Order> findByItemsSellerId(Long id);

    @Query("SELECT DISTINCT o.shipping.city FROM Order o")
    List<String> findDistinctCities();

    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);

    List<Order> findByShippingCityIgnoreCase(String city);

    List<Order> findByOrderDateBetweenAndShippingCityIgnoreCase(
            LocalDateTime start,LocalDateTime end,String city
    );

}
