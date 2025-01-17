package com.nazanokumus.loan_api_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/loans/create").hasRole("ADMIN")
                        .requestMatchers("/api/loans/list", "/api/loans/installments", "/api/loans/pay").hasAnyRole("ADMIN", "CUSTOMER")
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("password")
                .roles("ADMIN")
                .build();

        UserDetails customer = User.withDefaultPasswordEncoder()
                .username("customer")
                .password("password")
                .roles("CUSTOMER")
                .build();

        return new InMemoryUserDetailsManager(admin, customer);
    }
}
