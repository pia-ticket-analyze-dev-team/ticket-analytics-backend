package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    Optional<UserAccount> findByEmailIgnoreCaseOrFirstNameIgnoreCaseOrLastNameIgnoreCase(
            String email,
            String firstName,
            String lastName
    );

    Optional<UserAccount> findByAgentId(UUID agentId);

    boolean existsByEmailIgnoreCase(String email);
}
