package com.education.takeit.user.repository;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;

import org.springframework.data.domain.Pageable;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

  boolean existsByEmailAndLoginType(String email, LoginType loginType);

  Optional<User> findByUserId(Long id);

  Page<User> findAll(Pageable pageable);
}
