package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

  private final AuthService authService;

  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    // CsrfTokenArgumentResolver가 자동으로 토큰을 주입합니다.
    // 단순히 호출하는 것만으로도 지연된(deferred) 토큰이 로드되어 응답 쿠키에 포함됩니다.
    log.debug("CSRF 토큰 요청 수신: {}", csrfToken.getToken());
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<UserDto> getMe(DiscodeitUserDetails userDetails) {
    // SecurityContext에 인증된 사용자 정보가 없는 경우
    if (userDetails == null) {
      log.warn("인증되지 않은 사용자의 내 정보 조회 요청");
      return ResponseEntity.status(401).build();
    }
    log.debug("현재 사용자 정보 조회: {}", userDetails.getUsername());
    return ResponseEntity.ok(userDetails.getUserDto());
  }

  @Override
  public ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request) {
    log.info("권한 수정 요청: 대상ID={}, 신규권한={}", request.userId(), request.newRole());
    // 실제 비즈니스 로직은 AuthService에서 처리합니다.
    return ResponseEntity.ok(authService.updateRole(request));
  }
}