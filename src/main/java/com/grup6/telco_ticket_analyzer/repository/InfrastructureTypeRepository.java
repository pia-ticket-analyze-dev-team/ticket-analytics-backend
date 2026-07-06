package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.entity.InfrastructureType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InfrastructureTypeRepository extends JpaRepository<InfrastructureType, UUID> {

    Optional<InfrastructureType> findByInfrastructureName(String infrastructureName);
}