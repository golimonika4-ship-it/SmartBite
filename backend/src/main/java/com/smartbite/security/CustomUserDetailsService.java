package com.smartbite.security;

import com.smartbite.model.User;
import com.smartbite.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load user by email or college roll number
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new UsernameNotFoundException("User identifier cannot be empty");
        }

        String clean = identifier.trim();
        return userRepository.findByEmailIgnoreCaseOrRollNumberIgnoreCase(clean, clean)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + clean));
    }

    /**
     * Helper lookup by database user ID
     */
    @Transactional(readOnly = true)
    public User loadUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + id));
    }
}
