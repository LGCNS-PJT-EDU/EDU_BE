package com.education.takeit.user.repository;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
  boolean existsByEmail(String email);

  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

  Optional<User> findByEmail(String email);

  boolean existsByEmailAndLoginType(String email, LoginType loginType);

  Optional<User> findByUserId(Long id);
}
