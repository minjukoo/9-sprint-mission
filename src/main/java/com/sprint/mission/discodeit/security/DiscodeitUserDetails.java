package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.UserDto;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  public DiscodeitUserDetails(UserDto userDto, String password) {
    // DTO에는 프론트엔드가 이해할 수 있는 순수 문자열(USER, ADMIN 등)을 담습니다.
    this.userDto = userDto;
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 시큐리티 내부 검증용으로만 ROLE_ 접두사를 붙여서 반환합니다.
    String roleWithPrefix = userDto.role().startsWith("ROLE_")
        ? userDto.role()
        : "ROLE_" + userDto.role();
    return Collections.singleton(new SimpleGrantedAuthority(roleWithPrefix));
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return userDto.email();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }
}