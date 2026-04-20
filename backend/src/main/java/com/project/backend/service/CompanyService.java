package com.project.backend.service;

import com.project.backend.entity.Company;
import com.project.backend.repository.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public ResponseEntity<?> saveCompany(Company company){
        if(company.getCompname() != null && companyRepository.existsByCompname(company.getCompname())){
            return new ResponseEntity<>("Company already exists...", HttpStatus.CONFLICT);
        }
        Company save = companyRepository.save(company);
        return  new ResponseEntity<>(save,HttpStatus.OK);
    }

    public List<Company> findAll() { return companyRepository.findAll(); }

//    public Company findById(Long id) { return repo.findById(id).orElseThrow(() -> new RuntimeException("Company not found")); }

    public ResponseEntity<?> getById(Long id){
        Optional<Company> byId = companyRepository.findById(id);
        if(byId.isPresent()){
            byId.get();
            return new ResponseEntity<>(companyRepository.findById(id),HttpStatus.OK);
        }
        return new ResponseEntity<>("Company not found...",HttpStatus.CONFLICT);
    }

    public ResponseEntity<?> update(Long id, Company company){
        Optional<Company> byId = companyRepository.findById(id);
        if(byId.isPresent()){
            Company company1 = byId.get();
            company1.setCompname(company.getCompname());
            company1.setLogoUrl(company.getLogoUrl());

            Company save = companyRepository.save(company1);
            return ResponseEntity.ok(save);
        }
        return new ResponseEntity<>("Company not found...", HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<?> delete(Long id){
        Optional<Company> byId = companyRepository.findById(id);
        if(byId.isPresent()) {
            companyRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "Record Deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", "Company Not Found"));
    }
}
