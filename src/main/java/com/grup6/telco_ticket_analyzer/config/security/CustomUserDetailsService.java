package com.grup6.telco_ticket_analyzer.config.security;

import com.grup6.telco_ticket_analyzer.model.UserAccount;
import com.grup6.telco_ticket_analyzer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        UserAccount account = userAccountRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UsernameNotFoundException("User account not found"));

        return new org.springframework.security.core.userdetails.User(
                account.getEmail(),
                account.getPasswordHash(),
                account.isActive(),
                true,
                true,
                true,
                authorities(account)
        );
    }

    private Collection<? extends GrantedAuthority> authorities(UserAccount account) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().getRoleCode()));
    }
}
