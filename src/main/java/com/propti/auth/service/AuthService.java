package com.propti.auth.service;

import com.propti.auth.dto.UserRegistrationRequest;
import com.propti.auth.entity.User;
import com.propti.auth.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        User user = new User(request);
        return userRepository.save(user);
    }
}
