package com.education.takeit.kafka.recommand.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recom_fail_log")
@EntityListeners(AuditingEntityListener.class)
public class RecomFailLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "error_code", nullable = false)
    private String errorCode;

    @Column(name = "error_message", nullable = false)
    private String errorMessage;

    @CreatedDate
    @Column(name = "created_dt", updatable = false, nullable = false)
    private LocalDateTime createdDt;
}
