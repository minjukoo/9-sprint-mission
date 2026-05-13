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
    // 프론트엔드 규격(ROLE_ADMIN 등)을 맞추기 위해
    // DTO를 생성할 때 role에 접두사가 없다면 붙여서 저장합니다.
    String roleWithPrefix = userDto.role().startsWith("ROLE_")
        ? userDto.role()
        : "ROLE_" + userDto.role();

    this.userDto = new UserDto(
        userDto.id(),
        userDto.username(),
        userDto.email(),
        userDto.profile(),
        roleWithPrefix,
        userDto.online()
    );
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 이미 ROLE_이 붙은 role을 권한으로 반환합니다.
    return Collections.singleton(new SimpleGrantedAuthority(userDto.role()));
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return userDto.email(); // 이메일을 로그인 ID(username)로 사용
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
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DiscodeitUserDetails that = (DiscodeitUserDetails) o;
    // 세션 중복 체크 등을 위해 ID 기준으로 비교합니다.
    return Objects.equals(userDto.id(), that.userDto.id());
  }

  @Override
  public int hashCode() {
    return Objects.hash(userDto.id());
  }
}