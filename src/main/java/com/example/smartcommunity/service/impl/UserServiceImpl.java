package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.dto.CreateUserRequest;
import com.example.smartcommunity.exception.ResourceNotFoundException;
import com.example.smartcommunity.model.Admin;
import com.example.smartcommunity.model.Citizen;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.UserService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User tidak ditemukan"));
    }

    public User create(CreateUserRequest request) {
        User user = "ADMIN".equalsIgnoreCase(request.role) ? new Admin() : new Citizen();
        user.setNama(request.nama);
        user.setEmail(request.email);
        user.setPassword(request.password);
        user.setRole(request.role.toUpperCase());
        return userRepository.save(user);
    }

    public User update(Long id, CreateUserRequest request) {
        User user = findById(id);
        user.setNama(request.nama);
        user.setEmail(request.email);
        user.setPassword(request.password);
        user.setRole(request.role.toUpperCase());
        return userRepository.save(user);
    }

    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
