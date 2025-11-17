package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    public User createUser(User user, User.Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();

        // ⚡ Correction : utiliser orElseThrow() sur l'Optional
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new SecurityException("Utilisateur courant introuvable"));

        if (currentUser.getRole() == User.Role.SUPER_ADMIN && (role == User.Role.ADMIN || role == User.Role.USER)) {
            return saveUser(user, role);
        } else if (currentUser.getRole() == User.Role.ADMIN && role == User.Role.USER) {
            return saveUser(user, role);
        } else {
            throw new SecurityException("Vous n'êtes pas autorisé à créer ce type d'utilisateur.");
        }
    }

    private User saveUser(User user, User.Role role) {
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }


    /**
     * Register public (role facultatif)
     */
    public User register(User user) {
        user.setId(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // 👉 Utiliser le rôle fourni, sinon USER par défaut
        if (user.getRole() == null) {
            user.setRole(User.Role.USER);
        }

        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    /**
     * Update utilisateur
     */
    public User updateUser(String id, User updatedUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (updatedUser.getUsername() != null)
            user.setUsername(updatedUser.getUsername());

        if (updatedUser.getEmail() != null)
            user.setEmail(updatedUser.getEmail());

        if (updatedUser.getPassword() != null)
            user.setPassword(passwordEncoder.encode(updatedUser.getPassword()));

        if (updatedUser.getRole() != null)
            user.setRole(updatedUser.getRole());

        return userRepository.save(user);
    }

    /**
     * Delete user
     */
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        userRepository.deleteById(id);
    }

    /**
     *
     * @return
     */
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            throw new RuntimeException("Aucun utilisateur trouvé");
        }

        return users;
    }


    public User findByUsername(String username) {
        // ⚡ Correction : utiliser orElseThrow() pour récupérer le User
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}
