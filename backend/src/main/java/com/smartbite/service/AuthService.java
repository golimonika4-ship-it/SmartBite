package com.smartbite.service;

import com.smartbite.dto.AuthResponse;
import com.smartbite.dto.LoginRequest;
import com.smartbite.dto.RegisterRequest;
import com.smartbite.dto.UserProfileResponse;
import com.smartbite.model.Role;
import com.smartbite.model.User;
import com.smartbite.repository.UserRepository;
import com.smartbite.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new student account
     */
    public AuthResponse registerStudent(RegisterRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration request cannot be empty");
        }

        String cleanEmail = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String cleanRoll = request.getRollNumber() != null ? request.getRollNumber().trim().toUpperCase() : "";
        String cleanName = request.getName() != null ? request.getName().trim() : "";

        // Check duplicate email
        if (userRepository.existsByEmailIgnoreCase(cleanEmail)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email address already exists");
        }

        // Check duplicate roll number
        if (userRepository.existsByRollNumberIgnoreCase(cleanRoll)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this college roll number already exists");
        }

        // Password strength check
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password must be at least 6 characters long");
        }

        // Hash password with BCrypt
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                cleanName,
                cleanRoll,
                cleanEmail,
                hashedPassword,
                Role.STUDENT
        );

        User savedUser = userRepository.save(user);
        String token = jwtTokenProvider.generateToken(savedUser);

        return new AuthResponse(
                token,
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getRollNumber(),
                savedUser.getEmail(),
                savedUser.getRole().name(),
                "Student account registered successfully"
        );
    }

    /**
     * Authenticate student using email or roll number
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        if (request == null || request.getIdentifier() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier and password are required");
        }

        String cleanIdentifier = request.getIdentifier().trim();
        User user = userRepository.findByEmailIgnoreCaseOrRollNumberIgnoreCase(cleanIdentifier, cleanIdentifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email/roll number or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email/roll number or password");
        }

        // Student login is strictly for student accounts
        if (user.getRole() == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator accounts must sign in using the Admin Portal");
        }

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getRollNumber(),
                user.getEmail(),
                user.getRole().name(),
                "Student login successful"
        );
    }

    /**
     * Authenticate canteen administrator securely using email or admin ID
     */
    @Transactional(readOnly = true)
    public AuthResponse loginAdmin(LoginRequest request) {
        if (request == null || request.getIdentifier() == null || request.getPassword() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Identifier and password are required");
        }

        String cleanIdentifier = request.getIdentifier().trim();
        User user = userRepository.findByEmailIgnoreCaseOrRollNumberIgnoreCase(cleanIdentifier, cleanIdentifier)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid admin credentials");
        }

        // Strictly enforce ROLE_ADMIN
        if (user.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied: Account does not possess administrative privileges");
        }

        String token = jwtTokenProvider.generateToken(user);

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getRollNumber(),
                user.getEmail(),
                user.getRole().name(),
                "Admin login successful"
        );
    }

    /**
     * Authenticate canteen administrator in DEMO MODE without password prompt
     */
    @Transactional(readOnly = true)
    public AuthResponse loginDemoAdmin() {
        User adminUser = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No admin account configured in database"));

        String token = jwtTokenProvider.generateToken(adminUser);

        return new AuthResponse(
                token,
                adminUser.getId(),
                adminUser.getName(),
                adminUser.getRollNumber(),
                adminUser.getEmail(),
                adminUser.getRole().name(),
                "Demo Admin access granted"
        );
    }

    /**
     * Map User entity to UserProfileResponse
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getCurrentUserProfile(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No authenticated session found");
        }
        return new UserProfileResponse(
                user.getId(),
                user.getName(),
                user.getRollNumber(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
