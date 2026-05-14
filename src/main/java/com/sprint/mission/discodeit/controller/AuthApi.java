package com.sprint.mission.discodeit.controller;

import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
public interface AuthApi {

  @Operation(summary = "CSRF 토큰 발급")
  @GetMapping("/csrf-token")
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "현재 사용자 정보 조회")
  @GetMapping("/me")
  ResponseEntity<java.util.Map<String, Object>> getMe(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails);

  @Operation(summary = "사용자 권한 수정")
  @PutMapping("/role")
  ResponseEntity<UserDto> updateRole(@Valid @RequestBody UserRoleUpdateRequest request);

  @Operation(summary = "세션 갱신")
  @PostMapping("/refresh")
  ResponseEntity<java.util.Map<String, Object>> refresh(
      @AuthenticationPrincipal DiscodeitUserDetails userDetails);
}