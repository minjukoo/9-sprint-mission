package com.sprint.mission.discodeit.security;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiscodeitUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;
  private final UserMapper userMapper; // 엔티티를 Dto로 변환하기 위해 필요합니다

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    // 1. DB에서 이메일로 사용자를 조회합니다
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이메일의 사용자를 찾을 수 없습니다: " + email));

    // 2. 조회된 엔티티를 Dto로 변환하고 UserDetails 객체를 생성하여 반환합니다
    return new DiscodeitUserDetails(
        userMapper.toDto(user),
        user.getPassword()
    );
  }
}