package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import com.sprint.mission.discodeit.service.UserService;
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
  private final UserService userService;

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
    return ResponseEntity.ok(userService.findById(userDetails.getUserDto().id()));
  }

  // 추가 구현
  @Override
  public ResponseEntity<UserDto> refresh(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    if (userDetails == null) {
      log.warn("세션 갱신 실패: 인증 정보 없음");
      return ResponseEntity.status(401).build();
    }
    log.debug("세션 갱신 요청: {}", userDetails.getUsername());
    // 최신 온라인 상태 반영을 위해 userService.findById 사용
    return ResponseEntity.ok(userService.findById(userDetails.getUserDto().id()));
  }

  @Override
  public ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request) {
    log.info("권한 수정 요청: 대상ID={}, 신규권한={}", request.userId(), request.newRole());
    return ResponseEntity.ok(authService.updateRole(request));
  }
}