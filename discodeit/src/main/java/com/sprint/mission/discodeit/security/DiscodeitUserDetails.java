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
    // null 방지 및 ROLE_ 접두사 강제 부여
    String roleStr = userDto.role() == null ? "USER" : userDto.role();
    String roleWithPrefix = roleStr.startsWith("ROLE_") ? roleStr : "ROLE_" + roleStr;

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
    return Collections.singleton(new SimpleGrantedAuthority(userDto.role()));
  }

  @Override
  public String getPassword() { return this.password; }

  @Override
  public String getUsername() { return userDto.email(); }

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
  public int hashCode() { return Objects.hash(userDto.id()); }
}