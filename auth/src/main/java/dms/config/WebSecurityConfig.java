package dms.config;

import dms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

//    @Autowired
//    public WebSecurityConfig(UserService userService) {
//        this.userService = userService;
//    }

//    @Autowired
//    public void setUserService(UserService userService) {
//        this.userService = userService;
//    }

//    @Bean
//    public UserService userService() {
//        return new UserService();
//    }


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .cors();
        httpSecurity
                .csrf().disable();
        httpSecurity
                .httpBasic();
        httpSecurity
                .authorizeRequests()
//                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//                .antMatchers("/css/**", "/js/**").permitAll()
//                .antMatchers("/index.html").permitAll()
//                .antMatchers("/api/admin").permitAll()
//                .antMatchers("/api/admin").hasRole("ADMIN")
//                .anyRequest().authenticated()
                .anyRequest().permitAll()
        ;
//        httpSecurity
//                .formLogin()
//                .loginPage("/login")
//                .loginPage("http://localhost:4042/login/")
//                .defaultSuccessUrl("/main")
//                .permitAll();
        httpSecurity
                .logout()
                .logoutSuccessUrl("/login")
                .permitAll();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder());
        auth
                .inMemoryAuthentication()
                .withUser("user")
                .password(bCryptPasswordEncoder().encode("password"))
                .roles("USER")
                .and()
                .withUser("admin")
                .password(bCryptPasswordEncoder().encode("admin"))
                .roles("ADMIN");
    }


}
