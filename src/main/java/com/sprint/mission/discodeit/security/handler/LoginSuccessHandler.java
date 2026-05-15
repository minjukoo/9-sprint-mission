package com.sprint.mission.discodeit.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.security.DiscodeitUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
      log.info("!!! 핸들러가 호출되었습니다 !!!"); // 이 로그를 추가
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    // [중요] 프론트엔드 코드 규격에 맞게 Key 이름을 "userDto"로 설정하고,
    // 비어있지 않은 accessToken 값을 함께 보냅니다.
    Map<String, Object> responseBody = Map.of(
        "aaaaaTest", userDetails.getUserDto(),
        "accessToken", "session-based-auth" // 아무 문자열이나 들어가면 통과됩니다.
    );

    objectMapper.writeValue(response.getWriter(), responseBody);
  }
}