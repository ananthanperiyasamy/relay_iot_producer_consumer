package com.iot.relay.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.iot.relay.api.service.JwtUserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public WebSecurityConfig(JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        config.setAllowCredentials(true);                 // allow cookies/auth headers
        config.addAllowedOrigin("*"); 					  // frontend origin
        config.addAllowedHeader("*");                     // allow all headers
        config.addAllowedMethod("*");                     // allow GET, POST, PUT, DELETE, etc.
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
	   http.csrf().disable()
       .cors() // <-- enable CORS
       .and()
       .authorizeRequests()
           .antMatchers("/authenticate", "/create/user", "/v3/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
           .anyRequest().authenticated()
       .and()
           .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
       .and()
           .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

       http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

       return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, JwtUserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                   .userDetailsService(userDetailsService)
                   .passwordEncoder(passwordEncoder())
                   .and()
                   .build();
    }
}
