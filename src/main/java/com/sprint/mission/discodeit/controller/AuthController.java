package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.controller.api.AuthApi;
import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import com.sprint.mission.discodeit.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthApi {

  private final AuthService authService;

  @GetMapping("csrf-token")
  @Override
  public ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken) {
    csrfToken.getToken(); // 명시적 호출로 토큰 초기화
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @GetMapping("me")
  @Override
  public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal DiscodeitUserDetails userDetails) {
    // 세션의 인증 정보(Principal)에서 직접 UserDto를 꺼내 반환
    return ResponseEntity.ok(userDetails.getUserDto());
  }

  @PutMapping("role")
  @PreAuthorize("hasRole('ADMIN')") // ADMIN 권한 체크
  @Override
  public ResponseEntity<UserDto> updateRole(@RequestBody UserRoleUpdateRequest request) {
    UserDto updatedUser = authService.updateRole(request);
    return ResponseEntity.ok(updatedUser);
  }
}