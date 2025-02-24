package edu.rico.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import edu.rico.security.auth.filters.JwtAuthenticationFilter;
import edu.rico.security.auth.filters.JwtValidationFilter;

@Configuration
public class SpringSecurityConfig {

    @Autowired
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
/*
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeRequests()
                .requestMatchers(HttpMethod.GET, "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("ADMIN");
                .anyRequest().authenticated()
                .and()
                .addFilter(new JwtAuthenticationFilter(authenticationManager()))
                .addFilter(new JwtValidationFilter(authenticationManager()))
                .csrf(config -> config.disable())
                .sessionManagement(management -> management.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
 */
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz 
                .requestMatchers(HttpMethod.GET, "/users").permitAll()
                .requestMatchers(HttpMethod.GET, "/users/{id}").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/users").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/users").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/users").hasAnyRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilter(new JwtAuthenticationFilter(authenticationManager()))
            .addFilter(new JwtValidationFilter(authenticationManager()))
            .csrf(csrf -> csrf.disable()) 
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); 
        return http.build();
    }
     


}
