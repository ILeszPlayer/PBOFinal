package com.example.smartcommunity.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception ex, Model model, HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path != null && path.startsWith("/api/")) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage() != null ? ex.getMessage() : "Terjadi kesalahan"));
        }
        model.addAttribute("message", ex.getMessage());
        return "error";
    }
}