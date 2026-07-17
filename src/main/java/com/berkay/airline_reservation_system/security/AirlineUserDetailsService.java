package com.berkay.airline_reservation_system.security;

import com.berkay.airline_reservation_system.model.AirlineUser;
import com.berkay.airline_reservation_system.repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AirlineUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AirlineUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<AirlineUser> user = userRepository.findByUsername(username);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException(username);
        }

        return User.builder()
                .username(user.get().getUsername())
                .password(user.get().getPasswordHash())
                .roles(user.get().getRole().name())
                .build();
    }
}
