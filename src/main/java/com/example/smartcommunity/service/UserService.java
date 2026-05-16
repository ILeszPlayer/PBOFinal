package com.example.smartcommunity.service;

import java.util.List;

import com.example.smartcommunity.model.User;

public interface UserService {

    List<User> getAllUsers();

    User saveUser(User user);

    void deleteUser(Long id);
}