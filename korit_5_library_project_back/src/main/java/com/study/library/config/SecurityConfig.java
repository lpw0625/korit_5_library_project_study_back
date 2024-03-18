package com.study.library.config;

import com.study.library.security.exception.AuthEntryPoint;
import com.study.library.security.filter.JwtAuthenticationFilter;
import com.study.library.security.filter.PermitAllFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PermitAllFilter permitAllFilter; // PermitAllFilter 빈을 주입받습니다.

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter; // JwtAuthenticationFilter 빈을 주입받습니다.

    @Autowired
    private AuthEntryPoint authEntryPoint;



    // BCryptPasswordEncoder 빈을 등록합니다.
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // HttpSecurity를 설정합니다.
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // CORS 설정을 활성화합니다.
        http.cors(); // 안적어두면 크로스오리진 오류가 난다.
        // CSRF 보호를 비활성화합니다.
        http.csrf().disable();
        // 요청에 대한 인가 규칙을 설정합니다.
        http.authorizeRequests()
                // "/server/**" 및 "/auth/**"로 시작하는 요청은 모두 허용합니다.

                .antMatchers("/server/**", "/auth/**")
                .permitAll()
                // 그 외의 모든 요청은 인증이 필요합니다.
                .anyRequest()
                .authenticated()
                .and()
                // PermitAllFilter를 LogoutFilter 이후에 추가합니다.
                .addFilterAfter(permitAllFilter, LogoutFilter.class)
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 이전에 추가합니다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authEntryPoint);

                // 이필터가 끝나면 antMatcher가 실행이 된다 .
                // 필터가 통과하는 순간에 컨트롤러로 들어간다.
                // 막히면 컨트롤러로 못가면 인증이 필요하다고 계속 실행이 뜰 것이다.
    }
}

