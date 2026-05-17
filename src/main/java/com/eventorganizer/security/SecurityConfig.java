package com.eventorganizer.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.eventorganizer.security.jwt.AuthEntryPointJwt;
import com.eventorganizer.security.jwt.AuthTokenFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	@Autowired
	private AuthEntryPointJwt authEntryPointJwt;
	
    @Bean
    AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder(10);
	}

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
		configuration.setAllowCredentials(true);
		configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    	http.logout(logout->logout.disable());
    	http.csrf(csrf -> csrf.disable())
		 	.cors(cors -> cors
		 			.configurationSource(corsConfigurationSource()))
    		.exceptionHandling(exception -> exception.
    				authenticationEntryPoint(authEntryPointJwt))
    		.sessionManagement(session -> session
    				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    		.authorizeHttpRequests(auth ->
        		auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        			.requestMatchers("/signup/customer", "/signup/organizer", "/login").permitAll()
        			.requestMatchers(HttpMethod.GET, "/events/**").permitAll()
        			.anyRequest().authenticated()
				);
    
    	http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    
		return http.build();
	}
}
