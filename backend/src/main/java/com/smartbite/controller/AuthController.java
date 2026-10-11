package com.smartbite.controller;

import com.smartbite.dto.AuthResponse;
import com.smartbite.dto.LoginRequest;
import com.smartbite.dto.RegisterRequest;
import com.smartbite.dto.UserProfileResponse;
import com.smartbite.model.User;
import com.smartbite.security.JwtTokenProvider;
import com.smartbite.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @org.springframework.beans.factory.annotation.Value("${smartbite.demo-mode:true}")
    private boolean demoMode;

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * POST /api/auth/register
     * Register a new student account
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.registerStudent(request);
        setAuthCookie(response, authResponse.getToken(), jwtTokenProvider.getExpirationMs() / 1000);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    /**
     * POST /api/auth/login
     * Authenticate student using email/roll number and password
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setAuthCookie(response, authResponse.getToken(), jwtTokenProvider.getExpirationMs() / 1000);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/admin/login
     * Authenticate canteen administrator securely using admin email/ID and password
     */
    @PostMapping("/admin/login")
    public ResponseEntity<AuthResponse> loginAdmin(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {
        AuthResponse authResponse = authService.loginAdmin(request);
        setAuthCookie(response, authResponse.getToken(), jwtTokenProvider.getExpirationMs() / 1000);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/demo/admin-login
     * Automatically log into admin portal when DEMO MODE is active
     */
    @PostMapping("/demo/admin-login")
    public ResponseEntity<AuthResponse> loginDemoAdmin(HttpServletResponse response) {
        if (!demoMode) {
            throw new org.springframework.web.server.ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Demo mode is disabled. Please sign in with administrator credentials."
            );
        }
        AuthResponse authResponse = authService.loginDemoAdmin();
        setAuthCookie(response, authResponse.getToken(), jwtTokenProvider.getExpirationMs() / 1000);
        return ResponseEntity.ok(authResponse);
    }

    /**
     * POST /api/auth/logout
     * Clear authentication cookie
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        setAuthCookie(response, "", 0);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    /**
     * GET /api/auth/me
     * Retrieve authenticated profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserProfileResponse profile = authService.getCurrentUserProfile(currentUser);
        return ResponseEntity.ok(profile);
    }

    private void setAuthCookie(HttpServletResponse response, String token, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(jwtTokenProvider.getCookieName(), token)
                .httpOnly(true)
                .secure(false) // Allow HTTP during local dev
                .sameSite("Lax")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
