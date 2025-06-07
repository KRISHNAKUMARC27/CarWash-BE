package com.sas.carwash.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
	private final Environment environment;

	public SecurityConfig(CustomUserDetailsService userDetailsService,
			JwtAuthenticationFilter jwtAuthenticationFilter, Environment environment) {
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
		this.environment = environment;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()) // Disable CSRF
				.cors(cors -> cors.configurationSource(corsConfigurationSource())) // Add CORS configuration
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET,
								"/", "/index.html", "/favicon.ico",
								"/static/**", "/**/*.js", "/**/*.css", "/**/*.ico", "/**/*.png", "/**/*.jpg",
								"/**/*.jpeg",

								// Frontend app routes to allow deep links
								"/pages/**",
								"/dashboard/**",
								"/card/**",
								"/spares/**",
								"/service/**",
								"/invoice/**",
								"/estimate/**",
								"/employee/**",
								"/attendance/**",
								"/appointment/**",
								"/expense/**",
								"/financials/**")
						.permitAll()
						.requestMatchers("/auth/**", "/user/signup/**", "/jobCard/getPhotos/**",
								"/jobCard/getPhotoUrl/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated()

				).addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT
																										// filter

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());

		if (activeProfiles.contains("prod")) {
			// Prod-specific config
			System.out.println("Prod profile is active");
			configuration.setAllowedOrigins(List.of("http://localhost:8080"));
			configuration.setAllowCredentials(true);
		} else {
			// Dev or others - allow all origins
			configuration.setAllowedOriginPatterns(List.of("*"));
			configuration.setAllowCredentials(false); // Use pattern to support credentials + wildcard
		}
		configuration.setAllowedOrigins(List.of("http://localhost:8080")); // Allow all origins
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "OPTIONS", "DELETE", "PATCH")); // Allowed
																											// methods
		configuration.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
		// No credentials

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
