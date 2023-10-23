package com.catveloper365.studyshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 특정 HTTP 요청에 대한 웹 기반 보안 구성
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        //CSRF 토큰 생성 설정
        http.csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());

        //폼 기반 로그인/로그아웃 설정
        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true); //로그아웃 후 세션을 전체 삭제할 지 여부 설정

        //요청에 대한 인증/인가 설정
        http.authorizeRequests()
                .mvcMatchers("/css/**", "/js/**", "/img/**").permitAll() //static 디렉터리는 인증 필요없음
                .mvcMatchers("/", "/members/**", "/item/**", "/images/**").permitAll() //인증 없이 모든 사용자가 접근 가능
                .mvcMatchers("/admin/**").hasRole("ADMIN") //해당 계정이 ADMIN 권한일 때만 접근 가능
                .anyRequest().authenticated(); //그 외 모든 요청은 인증이 필요

        //인증되지 않은 사용자가 리소스에 접근했을 때 수행되는 핸들러 등록
        http.exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());

        return http.build();
    }

    /**
     * 패스워드를 암호화할 때 사용할 패스워드 인코더를 빈으로 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
