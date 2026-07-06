package com.grup6.telco_ticket_analyzer.repository;

import com.grup6.telco_ticket_analyzer.model.IssueTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface IssueTopicRepository extends JpaRepository<IssueTopic, UUID> {

    Optional<IssueTopic> findByTopicName(String topicName);
}
