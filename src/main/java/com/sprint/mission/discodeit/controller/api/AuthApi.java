package com.sprint.mission.discodeit.controller.api;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest; // 신규 DTO 필요
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Auth", description = "인증 및 권한 API")
public interface AuthApi {

  // AuthApi.java
  @Operation(summary = "배포 테스트용 핑")
  @GetMapping("test/ping")
  ResponseEntity<String> ping();

  @Operation(summary = "CSRF 토큰 발급")
  @GetMapping("csrf-token")
  ResponseEntity<Void> getCsrfToken(CsrfToken csrfToken);

  @Operation(summary = "현재 사용자 정보 조회 (세션)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "조회 성공"),
      @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
  })
  ResponseEntity<UserDto> getCurrentUser(DiscodeitUserDetails userDetails);

  @Operation(summary = "사용자 권한 수정 (ADMIN 전용)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "수정 성공"),
      @ApiResponse(responseCode = "403", description = "권한 부족")
  })
  ResponseEntity<UserDto> updateRole(UserRoleUpdateRequest request);
}