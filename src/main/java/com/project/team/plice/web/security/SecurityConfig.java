package com.project.team.plice.web.security;

import com.project.team.plice.service.classes.LoginServiceImpl;
import com.project.team.plice.socialauth.oauth2.handler.OAuth2AuthenticationFailureHandler;
import com.project.team.plice.socialauth.oauth2.handler.OAuth2AuthenticationSuccessHandler;
import com.project.team.plice.socialauth.oauth2.service.CustomOAuth2AuthService;
import com.project.team.plice.socialauth.oauth2.service.CustomOidcUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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
    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    // sns Login
    private final CustomOAuth2AuthService customOAuth2AuthService;

    private final CustomOidcUserService customOidcUserService;

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;




    private static final String[] STATIC_WHITELIST = {
            "/img/**", "/css/**", "/js/**", "/upload-img/**"
    };

    private static final String[] DYNAMIC_WHITELIST = {
            "/", "/home", "/login/**", "/sign-up/**", "/join/**", "/join-success/**",
            "/term-service/**", "/term-of-service/**", "/marketing/**", "/use-personal/**",
            "/map/**", "/markers/**", "/find-data/**", "/find-apart/**",
            "/chat/**", "/webjars/**", "**/websocket/**", "/ws/**",
            "/post/**", "/story-detail/**", "/notice-detail/**",
            "/contents/**", "/inquiry/**", "/inquiry_write/**", "/watchlist/**",
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
                        .successHandler(authenticationSuccessHandler)
                        .failureUrl("/login/error")
                .and()
                    .logout()
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/home")
                .and()
                    .rememberMe()
                        .key("rememberMe")
                        .tokenValiditySeconds(3600)
                        .alwaysRemember(false)
                        .userDetailsService(loginService)
                        .tokenRepository(tokenRepository())
                .and()
                    .csrf().disable();

        http.sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false);


        // sns Login
        http
                .httpBasic().disable()
                .formLogin().disable()
                .csrf().disable()
                .cors()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .oidcUserService(customOidcUserService)
                .userService(customOAuth2AuthService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(STATIC_WHITELIST);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("00099990000").password(passwordEncoder().encode("1234")).roles("ADMIN");
        auth
                .userDetailsService(loginService).passwordEncoder(passwordEncoder());
    }























}