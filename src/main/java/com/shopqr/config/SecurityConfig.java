package com.shopqr.config;

import com.shopqr.security.AdminAccessDeniedHandler;
import com.shopqr.security.RoleBasedAuthenticationSuccessHandler;
import com.shopqr.security.ShopUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            RoleBasedAuthenticationSuccessHandler successHandler,
            ShopUserDetailsService userDetailsService,
            AdminAccessDeniedHandler adminAccessDeniedHandler) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/meal/scan", "/scan"))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup", "/css/**", "/error").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/api/meal/scan", "/scan").permitAll()
                        .requestMatchers("/api/meal/qr-token/**", "/qr-token/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasAnyRole("USER", "MANAGER")
                        .anyRequest().authenticated())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(adminAccessDeniedHandler))
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("phoneNumber")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll())
                .userDetailsService(userDetailsService);
        http.cors(AbstractHttpConfigurer::disable);
        return http.build();
    }
}
