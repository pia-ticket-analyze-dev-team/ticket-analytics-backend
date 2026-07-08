package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    Optional<UserRole> findByRoleCodeIgnoreCase(String roleCode);
}
