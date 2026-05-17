package com.example.smartcommunity.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.smartcommunity.model.User;
import com.example.smartcommunity.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/users")
    public String users(HttpSession session, Model model){

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        model.addAttribute("user", new User());
        model.addAttribute("users", userService.getAllUsers());

        model.addAttribute("loggedUserName", session.getAttribute("loggedUserName"));
        model.addAttribute("loggedUserRole", session.getAttribute("loggedUserRole"));

        return "users";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, HttpSession session){

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        userService.saveUser(user);

        return "redirect:/users";
    }

    @GetMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session){

        if (session.getAttribute("loggedUser") == null) {
            return "redirect:/";
        }

        userService.deleteUser(id);

        return "redirect:/users";
    }
}