package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.entity.base.BaseUpdatableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUpdatableEntity {

  @Column(length = 50, nullable = false, unique = true)
  private String username;

  @Column(length = 100, nullable = false, unique = true)
  private String email;

  @Column(length = 60, nullable = false)
  private String password;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "profile_id", columnDefinition = "uuid")
  private BinaryContent profile;

  // 1. UserStatus 필드 삭제

  // 2. 권한(Role) 필드 추가
  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private UserRole role;

  public User(String username, String email, String password, BinaryContent profile, UserRole role) {
    this.username = username;
    this.email = email;
    this.password = password;
    this.profile = profile;
    this.role = (role != null) ? role : UserRole.USER; // 기본값 USER 설정
  }

  public void update(String newUsername, String newEmail, String newPassword,
      BinaryContent newProfile, UserRole newRole) {
    if (newUsername != null && !newUsername.equals(this.username)) {
      this.username = newUsername;
    }
    if (newEmail != null && !newEmail.equals(this.email)) {
      this.email = newEmail;
    }
    if (newPassword != null && !newPassword.equals(this.password)) {
      this.password = newPassword;
    }
    if (newProfile != null) {
      this.profile = newProfile;
    }
    if (newRole != null) {
      this.role = newRole;
    }
  }
}