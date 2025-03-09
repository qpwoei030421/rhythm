package com.ex.rhythm;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration // 스프링의 환경 설정 파일임을 의미하는 어노테이션
@EnableWebSecurity // 모든 요청 URL이 스프링 시큐리티의 제어를 받도록 만드는 어노테이션
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 클릭재킹 방지
				.headers(headers -> headers
						.addHeaderWriter(new XFrameOptionsHeaderWriter(
								XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))

				// 모든 요청을 인증된 사용자만 접근 가능하도록 설정
				.authorizeHttpRequests(auth -> auth
						// 정적 파일 및 로그인이 불필요한 페이지는 허용
						.requestMatchers("/css/**", "/js/**", "/img/**", "/vendor/**",
								"/user/login", "/user/findPw", "/user/signUp", "/user/emailOverlappingCheck",
								"/user/signup")
						.permitAll()
						.anyRequest().authenticated() // 나머지는 로그인한 사용자만 접근 가능
				)

				// 로그인 설정
				.formLogin(form -> form
						.loginPage("/user/login") // 커스텀 로그인 페이지
						.defaultSuccessUrl("/main", true) // 로그인 성공 후 리디렉트할 페이지
						.permitAll() // 로그인 페이지 접근 허용
				)

				// 로그아웃 설정
				.logout(logout -> logout
						.logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
						.logoutSuccessUrl("/user/login") // 로그아웃 후 로그인 페이지로 이동
						.invalidateHttpSession(true))

				// 세션 관리 (상태 저장 방식 활성화)
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

		SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_THREADLOCAL);

		// 설정 후 SecurityFilterChain 반환
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// AuthenticationManager : 시큐리티의 인증을 처리
	@Bean
	public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder) throws Exception {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder);

		return new ProviderManager(authProvider);
	}

}
