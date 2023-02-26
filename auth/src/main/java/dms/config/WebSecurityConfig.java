package dms.config;


import dms.exception.handler.CustomAccessDeniedHandler;
import dms.exception.handler.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;


//@Configuration
//@EnableWebSecurity
public class WebSecurityConfig {

//    @Bean
//    public BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return new CustomAccessDeniedHandler();
//    }
//
//    @Bean
//    public AuthenticationEntryPoint authEntryPoint() {
//        return new CustomAuthenticationEntryPoint();
//    }
//
//    @Bean
//    protected SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
//
//        httpSecurity
//                .cors();
//        httpSecurity
//                .csrf().disable();
//        httpSecurity
//                .httpBasic();
//        httpSecurity
//                .authorizeRequests()
////                .antMatchers("/api/devices/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling()
//                .authenticationEntryPoint(authEntryPoint())
//                .accessDeniedHandler(accessDeniedHandler())
//        ;
//        return httpSecurity.build();
//    }
}
