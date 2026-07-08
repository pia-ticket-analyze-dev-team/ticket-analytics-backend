package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID>, JpaSpecificationExecutor<Customer> {

    Optional<Customer> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<Customer> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String firstName,
            String lastName,
            String email,
            String phone,
            Pageable pageable
    );

    Page<Customer> findBySegment(String segment, Pageable pageable);

    Page<Customer> findByCreatedAtBetween(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    //total customer count as of a cutoff (for month-over-month KPI comparisons)
    long countByCreatedAtBefore(LocalDate cutoff);

}