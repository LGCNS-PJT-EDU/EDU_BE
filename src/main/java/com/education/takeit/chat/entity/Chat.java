package com.education.takeit.chat.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "chat")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Chat {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long chatId;

  @Column(name = "user_message", nullable = false)
  private String userMessage;

  @Column(name = "ai_message", nullable = false)
  private String aiMessage;

  @UpdateTimestamp
  @Column(name = "chat_timestamp")
  private LocalDateTime chatTimestamp;

  @Column(name = "user_id", nullable = false)
  private Long userId;
}
