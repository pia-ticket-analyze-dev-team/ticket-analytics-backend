package com.grup6.telco_ticket_analyzer.service.auth;

import com.grup6.telco_ticket_analyzer.dto.auth.LoginRequestDto;
import com.grup6.telco_ticket_analyzer.dto.auth.LoginResponseDto;
import com.grup6.telco_ticket_analyzer.exception.UnauthorizedException;
import com.grup6.telco_ticket_analyzer.model.UserAccount;
import com.grup6.telco_ticket_analyzer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public LoginResponseDto login(LoginRequestDto request) {
        String email = request == null ? null : request.email();
        String password = request == null ? null : request.password();

        if (!StringUtils.hasText(email) || !StringUtils.hasText(password)) {
            throw new UnauthorizedException();
        }

        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(email.trim())
                .orElseThrow(UnauthorizedException::new);

        if (!userAccount.isActive()
                || !StringUtils.hasText(userAccount.getPasswordHash())
                || !passwordEncoder.matches(password, userAccount.getPasswordHash())) {
            throw new UnauthorizedException();
        }

        String fullName = buildFullName(userAccount.getFirstName(), userAccount.getLastName());
        String roleCode = userAccount.getRole() != null ? userAccount.getRole().getRoleCode() : null;
        String departmentCode = null;
        if (userAccount.getAgent() != null && userAccount.getAgent().getDepartment() != null) {
            departmentCode = userAccount.getAgent().getDepartment().getDepartmentCode();
        }

        return new LoginResponseDto(
                userAccount.getId(),
                userAccount.getEmail(),
                fullName,
                roleCode,
                userAccount.getAgent() != null ? userAccount.getAgent().getId() : null,
                departmentCode
        );
    }

    private String buildFullName(String firstName, String lastName) {
        String left = firstName == null ? "" : firstName;
        String right = lastName == null ? "" : lastName;
        return (left + " " + right).trim();
    }
}
