package com.education.takeit.user.repository;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);
  boolean existsByEmailAndLoginType(String email, LoginType loginType);
  Optional<User> findByUserId(Long id);
}
