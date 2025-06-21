package com.pcs.serviceImpl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.pcs.dto.AdminRegisterRequest;
import com.pcs.model.Admin;
import com.pcs.repository.AdminRepository;
import com.pcs.service.AdminService;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean authenticate(String username, String rawPassword) {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        return admin.isPresent() && passwordEncoder.matches(rawPassword, admin.get().getPassword());
    }

    @Override
    public Admin register(AdminRegisterRequest request) {
        if (existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        Admin admin = new Admin();
        admin.setUsername(request.getUsername());
        admin.setPassword(passwordEncoder.encode(request.getPassword())); // 🔒 Hash password
        return adminRepository.save(admin);
    }

    @Override
    public boolean existsByUsername(String username) {
        return adminRepository.findByUsername(username).isPresent();
    }
}
