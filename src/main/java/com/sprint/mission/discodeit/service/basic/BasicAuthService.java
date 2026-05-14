package com.sprint.mission.discodeit.service.basic;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.exception.user.UserNotFoundException;
import com.sprint.mission.discodeit.mapper.UserMapper;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicAuthService implements AuthService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final SessionRegistry sessionRegistry;

  @Override
  @Transactional
  public UserDto updateRole(UserRoleUpdateRequest request) {
    log.info("사용자 권한 변경 시작: userId={}, newRole={}", request.userId(), request.newRole());

    // 1. 사용자 조회 및 권한 업데이트
    User user = userRepository.findById(request.userId())
        .orElseThrow(() -> new UserNotFoundException(request.userId().toString()));

    user.updateRole(request.newRole());
    userRepository.save(user);

    // 2. 심화 요구사항: 권한이 변경된 사용자의 세션 무효화
    invalidateUserSessions(user.getEmail());

    log.info("사용자 권한 변경 및 세션 무효화 완료: userId={}", user.getId());
    return userMapper.toDto(user);
  }

  /**
   * SessionRegistry에서 해당 사용자의 모든 세션을 찾아 만료시킵니다.
   */
  private void invalidateUserSessions(String email) {
    List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
    for (Object principal : allPrincipals) {
      if (principal instanceof DiscodeitUserDetails userDetails) {
        if (userDetails.getUsername().equals(email)) {
          // 해당 사용자의 모든 세션 정보를 가져와 만료 처리
          List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
          for (SessionInformation session : sessions) {
            session.expireNow();
            log.debug("세션 강제 만료 처리됨: sessionId={}", session.getSessionId());
          }
        }
      }
    }
  }
}