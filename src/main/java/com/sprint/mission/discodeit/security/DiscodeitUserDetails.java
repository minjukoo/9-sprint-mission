package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.data.UserDto;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@RequiredArgsConstructor
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // "ROLE_" 접두어를 붙여 권한 계층 구조(RoleHierarchy)와 연동합니다
    return List.of(new SimpleGrantedAuthority("ROLE_" + userDto.role().name()));
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    // 이메일을 식별자(Username)로 사용합니다
    return userDto.email();
  }

  // 세션 고도화: 동일 계정 동시 로그인 제어를 위해 id 기반으로 동일성 보장
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    return Objects.equals(userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }
}