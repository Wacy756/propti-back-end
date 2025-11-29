package com.propti.auth.service;

import com.propti.auth.dto.UserDto;
import com.propti.auth.entity.User;
import com.propti.auth.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserDto getUser(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
        return toDto(user);
    }

    @Transactional(readOnly = true)
    public Optional<User> findOptional(UUID id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findOptionalByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public UserDto getByEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + email));
        return toDto(user);
    }

    @Transactional
    public UserDto upsertName(String email, String role, String name) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User created = new User();
            created.setEmail(email);
            created.setRole(role != null ? role : "tenant");
            return created;
        });
        user.setName(name);
        userRepository.save(user);
        return toDto(user);
    }

    @Transactional
    public UserDto upsertRole(String email, String role, String name) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User created = new User();
            created.setEmail(email);
            return created;
        });
        user.setRole(role);
        if (name != null && user.getName() == null) {
            user.setName(name);
        }
        userRepository.save(user);
        return toDto(user);
    }

    private UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getName(),
                user.getPhone(),
                user.getCompanyName(),
                user.getCreatedAt()
        );
    }

    @Transactional
    public UserDto upsertProfile(String email, String role, String name, String phone, String companyName) {
        User user = userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            User created = new User();
            created.setEmail(email);
            created.setRole(role != null ? role : "tenant");
            return created;
        });
        if (name != null) user.setName(name);
        if (phone != null) user.setPhone(phone);
        if (companyName != null) user.setCompanyName(companyName);
        userRepository.save(user);
        return toDto(user);
    }
}
