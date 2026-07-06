package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.Agent;
import com.grup6.telco_ticket_analyzer.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AgentRepository extends JpaRepository<Agent, UUID> {

    List<Agent> findByDepartment(Department department);

    List<Agent> findByDepartmentId(UUID departmentId);

    
}