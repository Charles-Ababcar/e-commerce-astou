package com.example.demo.service;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.ShopResponseDTO;
import com.example.demo.dto.request.UserRegisterDTO;
import com.example.demo.dto.request.UserRequestDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.model.Shop;
import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Lazy
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Créer un utilisateur (par un admin ou super-admin)
     */
    public UserResponseDTO createUser(UserRequestDTO dto) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new SecurityException("Utilisateur courant introuvable"));

        User.Role role = dto.getRole() != null ? dto.getRole() : User.Role.USER;

        if ((currentUser.getRole() == User.Role.SUPER_ADMIN && (role == User.Role.ADMIN || role == User.Role.USER))
                || (currentUser.getRole() == User.Role.ADMIN && role == User.Role.USER)) {

            User user = new User();
            user.setName(dto.getName());
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(role);
            user.setCreatedAt(LocalDateTime.now());

            return convertToDto(userRepository.save(user));
        }

        throw new SecurityException("Vous n'êtes pas autorisé à créer ce type d'utilisateur.");
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    /**
     * Inscription publique (role USER par défaut)
     */
    public UserResponseDTO register(UserRegisterDTO dto) {
        User user = new User();
        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole(User.Role.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return convertToDto(userRepository.save(user));
    }

    /**
     * Mettre à jour un utilisateur
     */
    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getUsername() != null) user.setUsername(dto.getUsername());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(passwordEncoder.encode(dto.getPassword()));
        if (dto.getRole() != null) user.setRole(dto.getRole());

        return convertToDto(userRepository.save(user));
    }

    /**
     * Supprimer un utilisateur
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Utilisateur introuvable");
        }
        userRepository.deleteById(id);
    }

    /**
     * Récupérer tous les utilisateurs
     */
    public ApiResponse<Page<UserResponseDTO>> getAllUsers(String search, Pageable pageable) {

        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<User> usersPage;

        if (search == null || search.trim().isEmpty()) {
            usersPage = userRepository.findAll(sortedPageable);
        } else {
            usersPage = userRepository.searchUsers(search.trim(), sortedPageable);
        }

        // 🔥 Filtrer ici (avant le mapping)
        List<User> filtered =
                usersPage.getContent().stream()
                        .filter(user -> user.getRole() != User.Role.SUPER_ADMIN) // 👈 CORRECT
                        .toList();

        // 🔥 Recréer une PAGE manuellement
        Page<User> filteredPage = new PageImpl<>(
                filtered,
                sortedPageable,
                filtered.size()
        );

        Page<UserResponseDTO> dtoPage =
                filteredPage.map(this::convertToDto);

        return new ApiResponse<>(
                "Liste des utilisateurs récupérée avec succès",
                dtoPage,
                HttpStatus.OK.value()
        );
    }


    /**
     * Récupérer un utilisateur par ID
     */
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        return convertToDto(user);
    }



    /**
     * Changer son propre mot de passe (authentifié)
     */
    public void updatePasswordSelf(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Changer le mot de passe d'un utilisateur (par un admin)
     */
    public void updatePasswordByAdmin(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Convertir User -> UserResponseDTO
     */
    private UserResponseDTO convertToDto(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setCreatedAt(user.getCreatedAt());

        return dto;
    }
}
