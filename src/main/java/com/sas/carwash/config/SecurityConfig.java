package com.sas.carwash.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	private final CustomUserDetailsService userDetailsService;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;

	public SecurityConfig(CustomUserDetailsService userDetailsService,
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS configuration
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/", "/index.html", "/static/**", "/**/*.js", "/**/*.css",
								"/**/*.ico", "/**/*.png", "/**/*.jpg", "/**/*.jpeg", "/employee/**")
						.permitAll().requestMatchers("/auth/**","/user/signup/**", "/jobCard/getPhotos/**").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated()

				).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT
																										// filter

		return http.build();
	}

	// Define the CORS configuration
//	@Bean
//	public CorsFilter corsFilter() {
//	    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//	    CorsConfiguration config = new CorsConfiguration();
//	    config.setAllowCredentials(true); // Allow cookies and credentials
//	    config.addAllowedOrigin("*"); // Replace with your frontend origin
//	    config.addAllowedHeader("*"); // Allow all headers
//	    config.addAllowedMethod("*"); // Allow all HTTP methods
//	    source.registerCorsConfiguration("/**", config);
//	    return new CorsFilter(source);
//	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Allow all origins
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH")); // Allowed
																											// methods
		configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
		configuration.setAllowCredentials(true); // No credentials

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration); // Apply CORS to all endpoints
		return source;
	}

	@Bean
	public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
		return http.getSharedObject(AuthenticationManagerBuilder.class).userDetailsService(userDetailsService)
				.passwordEncoder(passwordEncoder()).and().build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
