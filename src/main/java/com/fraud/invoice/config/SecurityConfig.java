package com.fraud.invoice.config;

import com.fraud.invoice.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private UserDetailsServiceImpl userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/vendors/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/invoices").hasRole("VENDOR")
                .requestMatchers("/api/invoices/*/approve").hasRole("FINANCE")
                .requestMatchers("/api/invoices/*/reject").hasRole("FINANCE")
                .requestMatchers("/api/invoices/flagged").hasAnyRole("FINANCE", "ADMIN")
                .requestMatchers("/api/invoices").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {});
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config)
        throws Exception {
        return config.getAuthenticationManager();
    }
}