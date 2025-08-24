package com.simba.project01;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 (React build 결과물) 및 HTML 페이지 허용
                        .requestMatchers("/", "/static/**", "/index.html","/upload/**", "/upload/*").permitAll()

                        // 인증/회원가입 API 허용
                        .requestMatchers("/itda/auth/**").permitAll()

                        // 조회(GET) API 허용
                        .requestMatchers(HttpMethod.GET,
                                "/itda/stores/**",
                                "/itda/missions/**",
                                "/itda/reviews/**"
                        ).permitAll()

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.disable())
                .httpBasic(b -> b.disable())
                .userDetailsService(userDetailsService)

                // 로그아웃 처리
                .logout(logout -> logout
                        .logoutUrl("/itda/auth/logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json;charset=UTF-8");
                            response.getWriter().write("{\"message\": \"로그아웃 성공\"}");
                        })
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}
