package com.project.backend.controller;

import com.project.backend.entity.Admin;
import com.project.backend.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody Admin admin){
        return adminService.create(admin);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin admin){
        return adminService.login(admin);
    }

}
