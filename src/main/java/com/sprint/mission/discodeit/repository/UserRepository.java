package com.sprint.mission.discodeit.repository;

import com.sprint.mission.discodeit.entity.User;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, UUID> {

  // u.status 부분을 삭제했습니다.
  @EntityGraph(attributePaths = {"profile"})
  @Query("SELECT u FROM User u")
  Page<User> findAll(Pageable pageable);

  Optional<User> findByUsername(String username);

  @EntityGraph(attributePaths = {"profile"})
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}