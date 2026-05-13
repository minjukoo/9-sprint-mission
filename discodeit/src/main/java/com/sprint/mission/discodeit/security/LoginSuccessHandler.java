package com.sprint.mission.discodeit.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sprint.mission.discodeit.dto.response.UserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final ObjectMapper objectMapper;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {
    DiscodeitUserDetails userDetails = (DiscodeitUserDetails) authentication.getPrincipal();
    UserDto originalDto = userDetails.getUserDto();

    // 인증 성공 핸들러에 도달했다는 것은 로그인이 성공했다는 의미이므로,
    // 프론트엔드가 메인 화면으로 진입할 수 있도록 online 필드를 true로 설정한 새로운 Dto를 생성합니다.
    UserDto successDto = new UserDto(
        originalDto.id(),
        originalDto.username(),
        originalDto.email(),
        originalDto.profile(),
        originalDto.role(),
        true // online 상태를 true로 강제 설정
    );

    response.setStatus(HttpServletResponse.SC_OK);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");

    objectMapper.writeValue(response.getWriter(), successDto);
  }
}