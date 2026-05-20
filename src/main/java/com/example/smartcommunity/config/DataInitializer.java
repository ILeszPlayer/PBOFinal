package com.example.smartcommunity.config;

import com.example.smartcommunity.model.Admin;
import com.example.smartcommunity.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail("admin@gmail.com")) {
            Admin admin = new Admin("Admin", "admin@gmail.com",
                    passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            System.out.println("✅ Admin default berhasil dibuat (admin@gmail.com / admin123)");
        }
    }
}
