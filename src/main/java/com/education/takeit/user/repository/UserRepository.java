package com.education.takeit.user.repository;

import com.education.takeit.user.entity.LoginType;
import com.education.takeit.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmailAndLoginType(String email, LoginType loginType);

  boolean existsByEmailAndLoginType(String email, LoginType loginType);

  Optional<User> findByUserId(Long id);

  Page<User> findAll(Pageable pageable);

  @Query(
      "SELECT u FROM User u "
          + "WHERE (:nickname IS NULL OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :nickname, '%'))) "
          + "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%')))")
  Page<User> findByNicknameAndEmail(
      @Param("nickname") String nickname, @Param("email") String email, Pageable pageable);
}
