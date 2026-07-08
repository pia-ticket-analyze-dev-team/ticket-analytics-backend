package com.grup6.telco_ticket_analyzer.controller;

import com.grup6.telco_ticket_analyzer.dto.auth.LoginRequestDto;
import com.grup6.telco_ticket_analyzer.dto.auth.AuthResponseDto;
import com.grup6.telco_ticket_analyzer.dto.auth.SignupRequestDto;
import com.grup6.telco_ticket_analyzer.service.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(
            @Valid @RequestBody LoginRequestDto request,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.login(request, servletRequest, response));
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signup(
            @Valid @RequestBody SignupRequestDto request,
            HttpServletRequest servletRequest,
            HttpServletResponse response
    ) {
        return ResponseEntity.ok(authService.signup(request, servletRequest, response));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponseDto> me(Authentication authentication) {
        return ResponseEntity.ok(authService.currentUser(authentication));
    }
}
