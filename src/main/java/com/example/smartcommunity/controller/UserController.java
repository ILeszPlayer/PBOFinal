package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.CreateUserRequest;
import com.example.smartcommunity.model.User;
import com.example.smartcommunity.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping
    public List<User> getAllUsers() { return userService.findAll(); }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) { return userService.findById(id); }

    @PostMapping
    public User createUser(@Valid @RequestBody CreateUserRequest request) { return userService.create(request); }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @Valid @RequestBody CreateUserRequest request) { return userService.update(id, request); }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) { userService.delete(id); return ResponseEntity.noContent().build(); }
}
