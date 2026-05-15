package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException exception) throws IOException, ServletException {
    // 1. 응답 설정 (401 Unauthorized)
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // 2. 에러 메시지 구성 (기존 ErrorResponse 규격이 있다면 그에 맞게 수정 가능)
    Map<String, Object> errorResponse = Map.of(
        "status", HttpStatus.UNAUTHORIZED.value(),
        "message", "이메일 또는 비밀번호가 일치하지 않습니다.",
        "error", "Unauthorized"
    );

    // 3. JSON으로 변환하여 응답
    objectMapper.writeValue(response.getWriter(), errorResponse);
  }
}