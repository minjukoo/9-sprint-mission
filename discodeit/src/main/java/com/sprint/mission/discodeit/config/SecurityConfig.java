package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.security.LoginFailureHandler;
import com.sprint.mission.discodeit.security.LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final LoginSuccessHandler loginSuccessHandler;
  private final LoginFailureHandler loginFailureHandler;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // 1. CSRF 설정 (CSR/SPA 환경 최적화)
        .csrf(csrf -> csrf
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
        )

        // 2. 헤더 설정 (H2 Console 등을 위해 X-Frame-Options 비활성화)
        .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))

        // 3. 인가 설정
        .authorizeHttpRequests(auth -> auth
            // 퍼블릭 접근 허용 (명세서 요구사항)
            .requestMatchers("/api/auth/csrf-token").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users").permitAll() // 회원가입
            .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()

            // 정적 리소스 및 도구
            .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
            .requestMatchers("/h2-console/**").permitAll()
            .requestMatchers("/actuator/**").permitAll()

            // 나머지 모든 요청은 인증 필요
            .anyRequest().authenticated()
        )

        // 4. 로그인 설정 (JSON 응답을 위한 커스텀 핸들러 등록)
        .formLogin(login -> login
            .loginProcessingUrl("/api/auth/login")
            .successHandler(loginSuccessHandler)
            .failureHandler(loginFailureHandler)
            .permitAll()
        )

        // 5. 로그아웃 설정 (204 No Content 응답)
        .logout(logout -> logout
            .logoutUrl("/api/auth/logout")
            .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT))
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
        )

        // 6. 세션 관리 고도화 (심화 요구사항)
        .sessionManagement(session -> session
            .sessionConcurrency(concurrency -> concurrency
                .maximumSessions(1) // 동일 계정 동시 로그인 제한
                .sessionRegistry(sessionRegistry())
            )
        )

        // 7. 자동 로그인 (Remember-Me)
        .rememberMe(remember -> remember
            .key("discodeit-secret-key") // 서버의 고유 키
            .tokenValiditySeconds(60 * 60 * 24 * 7) // 7일 유지
            .rememberMeParameter("remember-me") // 파라미터명
        )

        // 8. 예외 처리 (인증/인가 실패 시 JSON 응답)
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((request, response, authException) ->
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED))
            .accessDeniedHandler((request, response, accessDeniedException) ->
                response.sendError(HttpServletResponse.SC_FORBIDDEN))
        );

    return http.build();
  }

  // --- 심화 요구사항을 위한 추가 Bean ---

  @Bean
  public SessionRegistry sessionRegistry() {
    return new SessionRegistryImpl();
  }

  // 세션 만료 이벤트를 SessionRegistry에 알리기 위해 필요
  @Bean
  public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
  }
}