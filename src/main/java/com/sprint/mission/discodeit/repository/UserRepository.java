package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByUsername(String username);

  // 1. 이메일로 사용자 찾기 추가 (로그인 시 필요)
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByUsername(String username);

  // 2. UserStatus가 삭제되었으므로 JOIN FETCH u.status 제거
  @Query("SELECT u FROM User u LEFT JOIN FETCH u.profile")
  List<User> findAllWithProfile();
}