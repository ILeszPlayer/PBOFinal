package com.example.smartcommunity.service;

import com.example.smartcommunity.dto.CreateUserRequest;
import com.example.smartcommunity.model.User;
import java.util.List;

public interface UserService {
    List<User> findAll();
    User findById(Long id);
    User create(CreateUserRequest request);
    User update(Long id, CreateUserRequest request);
    void delete(Long id);
}
