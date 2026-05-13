package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService; // 추가
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;
  private final UserService userService; // 추가: 사용자의 최신 온라인 상태 조회를 위해 필요

  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    log.debug("CSRF 토큰 요청 수신: {}", csrfToken.getToken());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserDto> getMe(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    if (userDetails == null) {
      log.warn("인증되지 않은 사용자의 내 정보 조회 요청");
      return ResponseEntity.status(401).build();
    }

    // 핵심 수정: 단순히 세션에 저장된(stale한) DTO를 반환하지 않고,
    // userService.findById를 호출하여 SessionRegistry 로직이 적용된(online: true) 최신 정보를 반환합니다.
    UserDto currentUser = userService.findById(userDetails.getUserDto().id());

    log.debug("현재 사용자 정보 조회 성공: {}, 온라인 여부: {}", currentUser.username(), currentUser.online());
    return ResponseEntity.ok(currentUser);
  }

  @Override
  public ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request) {
    log.info("권한 수정 요청: 대상ID={}, 신규권한={}", request.userId(), request.newRole());
    return ResponseEntity.ok(authService.updateRole(request));
  }
}