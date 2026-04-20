package com.project.backend.repository;

import com.project.backend.entity.Enquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EnquiryRepository extends JpaRepository<Enquiry,Long> {
}
