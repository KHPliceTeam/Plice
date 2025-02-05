package com.project.team.plice.web.security;

import com.project.team.plice.service.classes.LoginServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final LoginServiceImpl loginService;
    private final DataSource dataSource;
    private final LoginSuccessHandler loginSuccessHandler;

    private static final String[] STATIC_WHITELIST = {
            "/img/**", "/css/**", "/js/**", "/upload-img/**"
    };

    private static final String[] DYNAMIC_WHITELIST = {
            "/", "/home", "/login/**", "/sign-up/**", "/join/**", "/join-success/**",
            "/term-service/**", "/term-of-service/**", "/marketing/**", "/use-personal/**",
            "/map/**", "/markers/**", "/find-data/**", "/find-apart/**",
            "/chat/**", "/webjars/**", "**/websocket/**", "/ws/**",
            "/post/**", "/contents/**", "/inquiry/**", "/inquiry_write/**", "/watchlist/**",
            "/openapi.molit.go.kr/**", "/apis.data.go.kr/**", "/favicon.ico",
            "/dapi.kakao.com/**", "/map.kakao.com/**", "/t1.daumcdn.net/**"
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {  // 회원가입 시 비밀번호 암호화에 사용할 Encoder 빈 등록
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository tokenRepository(){
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                    .antMatchers(DYNAMIC_WHITELIST).permitAll()
                    .antMatchers("/admin").hasAnyRole("ADMIN", "SUPER_ADMIN")
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                        .loginPage("/login")
                        .usernameParameter("phone")
                        .passwordParameter("pw")
                        .loginProcessingUrl("/loginProc")
                        .successHandler(loginSuccessHandler)
                .and()
                    .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/home")
                .and()
                    .rememberMe()
                        .rememberMeParameter("rememberMe")
                        .key("rememberMe")
                        .tokenValiditySeconds(3600)
                        .alwaysRemember(false)
                        .userDetailsService(loginService)
                        .tokenRepository(tokenRepository())
                .and()
                    .csrf().disable();

        http.headers()
                    .frameOptions().sameOrigin()
                .and()
                    .sessionManagement()
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false);
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(STATIC_WHITELIST);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService).passwordEncoder(passwordEncoder());
    }
}