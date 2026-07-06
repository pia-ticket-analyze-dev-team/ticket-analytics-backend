package com.grup6.telco_ticket_analyzer.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "issue_topic")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "topic_name", nullable = false)
    private String topicName;

    @Column(name = "sla_target_hours", nullable = false)
    private Integer slaTargetHours;
}
