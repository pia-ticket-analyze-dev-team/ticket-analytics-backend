package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceTypeRepository extends JpaRepository<ServiceType, UUID> {

    Optional<ServiceType> findByServiceName(String serviceName);
}