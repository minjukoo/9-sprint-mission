package com.sprint.mission.discodeit.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.util.StringUtils;

import java.util.function.Supplier;

public class SpaCsrfTokenRequestHandler implements CsrfTokenRequestHandler {
  private final CsrfTokenRequestHandler plain = new CsrfTokenRequestAttributeHandler();
  private final CsrfTokenRequestHandler xor = new XorCsrfTokenRequestAttributeHandler();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, Supplier<CsrfToken> csrfToken) {
    /*
     * 응답 바디에 렌더링될 때 CsrfToken의 BREACH 보호를 제공하기 위해
     * 항상 XorCsrfTokenRequestAttributeHandler를 사용합니다.
     */
    this.xor.handle(request, response, csrfToken);
    /*
     * 지연된 토큰이 로드되도록 하여 토큰 값을 쿠키에 렌더링합니다.
     */
    csrfToken.get();
  }

  @Override
  public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
    String headerValue = request.getHeader(csrfToken.getHeaderName());
    /*
     * 요청에 헤더가 포함되어 있으면 CsrfTokenRequestAttributeHandler를 사용하여
     * CsrfToken을 해결합니다. 이는 SPA가 쿠키를 통해 얻은 원시 토큰 값을
     * 헤더에 자동으로 포함할 때 적용됩니다.
     */
    return (StringUtils.hasText(headerValue) ? this.plain : this.xor).resolveCsrfTokenValue(request, csrfToken);
  }
}