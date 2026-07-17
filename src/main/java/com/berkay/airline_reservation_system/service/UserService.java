package com.berkay.airline_reservation_system.service;

import com.berkay.airline_reservation_system.model.AirlineUser;
import com.berkay.airline_reservation_system.model.Role;
import com.berkay.airline_reservation_system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(String username, String rawPassword, String fullName){
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("Username already exists.");
        }

        AirlineUser newUser = new AirlineUser();
        newUser.setUsername(username);
        newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        newUser.setFullName(fullName);
        newUser.setRole(Role.USER);

        userRepository.save(newUser);
    }

    @Transactional(readOnly = true)
    public AirlineUser getByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
