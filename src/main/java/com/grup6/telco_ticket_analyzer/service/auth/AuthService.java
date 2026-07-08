package com.grup6.telco_ticket_analyzer.service.auth;

import com.grup6.telco_ticket_analyzer.dto.auth.AuthResponseDto;
import com.grup6.telco_ticket_analyzer.dto.auth.LoginRequestDto;
import com.grup6.telco_ticket_analyzer.dto.auth.SignupRequestDto;
import com.grup6.telco_ticket_analyzer.exception.ResourceConflictException;
import com.grup6.telco_ticket_analyzer.exception.UnauthorizedException;
import com.grup6.telco_ticket_analyzer.model.Agent;
import com.grup6.telco_ticket_analyzer.model.UserAccount;
import com.grup6.telco_ticket_analyzer.model.UserRole;
import com.grup6.telco_ticket_analyzer.repository.AgentRepository;
import com.grup6.telco_ticket_analyzer.repository.UserAccountRepository;
import com.grup6.telco_ticket_analyzer.repository.UserRoleRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final UserRoleRepository userRoleRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityContextRepository securityContextRepository;

    @Transactional
    public AuthResponseDto login(LoginRequestDto requestDto, jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.email(), requestDto.password())
        );

        if (!authentication.isAuthenticated()) {
            throw new UnauthorizedException("Invalid credentials");
        }

        UserAccount account = userAccountRepository.findByEmailIgnoreCase(requestDto.email())
                .orElseThrow(() -> new UnauthorizedException("User account not found"));

        persistAuthentication(authentication, request, response);
        return toResponse(account);
    }

    @Transactional
    public AuthResponseDto signup(SignupRequestDto requestDto, jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        String email = requestDto.email().trim();
        String roleCode = requestDto.roleCode().trim().toUpperCase();

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw new ResourceConflictException("Email is already in use");
        }

        UserRole role = userRoleRepository.findByRoleCodeIgnoreCase(roleCode)
                .orElseThrow(() -> new UnauthorizedException("Role not found"));

        Agent agent = resolveAgent(roleCode, requestDto.agentId());

        if (agent != null && userAccountRepository.findByAgentId(agent.getId()).isPresent()) {
            throw new ResourceConflictException("Agent already has an account");
        }

        UserAccount account = new UserAccount();
        account.setFirstName(requestDto.firstName().trim());
        account.setLastName(requestDto.lastName().trim());
        account.setEmail(email);
        account.setPasswordHash(passwordEncoder.encode(requestDto.password()));
        account.setRole(role);
        account.setAgent(agent);
        account.setActive(true);

        UserAccount savedAccount = userAccountRepository.saveAndFlush(account);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, requestDto.password())
        );
        persistAuthentication(authentication, request, response);
        return toResponse(savedAccount);
    }

    public AuthResponseDto currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Not authenticated");
        }

        return userAccountRepository.findByEmailIgnoreCase(authentication.getName())
                .map(this::toResponse)
                .orElseThrow(() -> new UnauthorizedException("User account not found"));
    }

    public void logout(jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        SecurityContextHolder.clearContext();
        var session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private Agent resolveAgent(String roleCode, java.util.UUID agentId) {
        if ("ADMIN".equals(roleCode)) {
            return null;
        }

        if (agentId == null) {
            throw new UnauthorizedException("Agent is required for non-admin users");
        }

        return agentRepository.findById(agentId)
                .orElseThrow(() -> new UnauthorizedException("Agent not found"));
    }

    private void persistAuthentication(Authentication authentication, jakarta.servlet.http.HttpServletRequest request, HttpServletResponse response) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);
    }

    private AuthResponseDto toResponse(UserAccount account) {
        return new AuthResponseDto(
                account.getId(),
                account.getEmail(),
                account.getFirstName(),
                account.getLastName(),
                account.getRole().getRoleCode(),
                account.getRole().getRoleName(),
                account.getAgent() == null ? null : account.getAgent().getId(),
                account.isActive()
        );
    }
}
