package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.dto.response.UserDto;
import java.util.Objects;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class DiscodeitUserDetails implements UserDetails {

  private final UserDto userDto;
  private final String password;

  // 생성자에서 UserDto의 role에 직접 "ROLE_"을 붙여서 재조립합니다.
  public DiscodeitUserDetails(UserDto userDto, String password) {
    this.userDto = new UserDto(
        userDto.id(),
        userDto.username(),
        userDto.email(),
        userDto.profile(),
        "ROLE_" + userDto.role().replace("ROLE_", ""), // 중복 방지 처리 포함
        userDto.online()
    );
    this.password = password;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 이미 ROLE_이 붙어있는 userDto.role()을 사용합니다.
    return Collections.singleton(new SimpleGrantedAuthority(userDto.role()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.email();
  }

  @Override
  public boolean isAccountNonExpired() { return true; }

  @Override
  public boolean isAccountNonLocked() { return true; }

  @Override
  public boolean isCredentialsNonExpired() { return true; }

  @Override
  public boolean isEnabled() { return true; }

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