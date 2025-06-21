package com.pcs.service;

import com.pcs.dto.AdminRegisterRequest;
import com.pcs.model.Admin;

public interface AdminService {
    boolean authenticate(String username, String password);
    Admin register(AdminRegisterRequest request);
    boolean existsByUsername(String username);
}
