package com.example.smartcommunity.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.smartcommunity.model.User;
import com.example.smartcommunity.repository.UserRepository;
import com.example.smartcommunity.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    @Override
    public User saveUser(User user){
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}