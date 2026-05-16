package com.example.smartcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.smartcommunity.model.User;
import com.example.smartcommunity.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(Model model){

        model.addAttribute("user", new User());
        model.addAttribute("users", userService.getAllUsers());

        return "users";
    }

    @PostMapping("/save")
    public String saveUser(@ModelAttribute User user){

        userService.saveUser(user);

        return "redirect:/users";
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable Long id){

        userService.deleteUser(id);

        return "redirect:/users";
    }
}