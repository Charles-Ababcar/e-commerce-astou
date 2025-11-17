package com.example.demo.config;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("superadmin").isEmpty()) {
            User superAdmin = new User();
            superAdmin.setId(UUID.randomUUID().toString());
            superAdmin.setUsername("superadmin");
            superAdmin.setPassword(passwordEncoder.encode("superadminpassword")); // Change this in a real application
            superAdmin.setRole(User.Role.SUPER_ADMIN);
            superAdmin.setCreatedAt(LocalDateTime.now());

            userRepository.save(superAdmin);
            System.out.println("Created SUPER_ADMIN user.");
        }
    }
}
