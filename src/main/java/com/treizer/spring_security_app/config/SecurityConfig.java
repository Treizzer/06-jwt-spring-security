package com.treizer.spring_security_app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.treizer.spring_security_app.config.filters.JwtTokenvalidator;
import com.treizer.spring_security_app.services.UserDetailService;
import com.treizer.spring_security_app.utils.JwtUtils;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Configure with annotations
public class SecurityConfig {

    @Autowired
    private JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(http -> {
                    // Configure the public endpoints
                    http.requestMatchers(HttpMethod.GET, "/api/auth/get").permitAll();
                    http.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll();

                    // Configure the private endpoints
                    // http.requestMatchers(HttpMethod.POST,
                    // "/api/auth/post").hasAnyAuthority("CREATE", "READ");
                    http.requestMatchers(HttpMethod.POST, "/api/method/post").hasAnyRole("ADMIN", "DEVELOPER");
                    http.requestMatchers(HttpMethod.PATCH, "/api/method/patch").hasAnyAuthority("REFACTOR");
                    http.requestMatchers(HttpMethod.GET, "/api/method/get").hasAnyAuthority("READ");
                    // http.requestMatchers(HttpMethod.GET,
                    // "/api/method/get").hasAnyRole("INVITED");

                    // Configure the rest of enpoints -
                    // Not specified
                    http.anyRequest().denyAll(); // Deny all
                    // http.anyRequest().authenticated(); // Allows anyone that has credentials
                })
                // Important to secure the jwt token validator works
                .addFilterBefore(new JwtTokenvalidator(this.jwtUtils), BasicAuthenticationFilter.class)
                .build();
    }

    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity)
    // throws Exception {
    // return httpSecurity
    // .csrf(csrf -> csrf.disable())
    // .httpBasic(Customizer.withDefaults())
    // .sessionManagement(session ->
    // session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    // .build();
    // }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailService userDetailService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        provider.setUserDetailsService(userDetailService);
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // return NoOpPasswordEncoder.getInstance(); // Only in testing
        return new BCryptPasswordEncoder();
    }

    // public static void main(String[] args) {
    // System.out.println(new BCryptPasswordEncoder().encode("1234"));
    // // $2a$10$vVgxw0q/lOhcEYKQrr.VbOtBkIU8j8HJNwFpzkhJf5NH3EgGd9QIe
    // }

}
