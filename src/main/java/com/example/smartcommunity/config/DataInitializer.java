package com.example.smartcommunity.config;

import com.example.smartcommunity.model.Admin;
import com.example.smartcommunity.model.Citizen;
import com.example.smartcommunity.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        try {
            if (userRepository.count() > 0) {
                log.info("Database already seeded ({} users). Skipping initialization.", userRepository.count());
                return;
            }

            Admin admin = new Admin("Admin", "admin@gmail.com",
                    passwordEncoder.encode("admin123"));
            userRepository.save(admin);
            log.info("Admin created: admin@gmail.com / admin123");

            Citizen citizen = new Citizen("Warga Baru", "warga@gmail.com",
                    passwordEncoder.encode("warga123"));
            userRepository.save(citizen);
            log.info("Citizen created: warga@gmail.com / warga123");

        } catch (Exception e) {
            log.error("DataInitializer failed — database may already be partially seeded. Error: {}", e.getMessage());
        }
    }
}
