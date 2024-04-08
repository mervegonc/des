package com.tobias.des.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.tobias.des.jwt.JwtAuthenticationEntryPoint;
import com.tobias.des.jwt.JwtAuthenticationFilter;

import lombok.AllArgsConstructor;

@Configuration
@EnableMethodSecurity
@AllArgsConstructor
public class SpringSecurityConfig {

	private UserDetailsService userDetailsService;

	private JwtAuthenticationEntryPoint authenticationEntryPoint;

	private JwtAuthenticationFilter authenticationFilter;

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests((authorize) -> {

			authorize.requestMatchers("/api/auth/signin").permitAll().requestMatchers("/api/auth/signup").permitAll();
			authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
			authorize.requestMatchers(HttpMethod.GET, "/api/user/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/user/profile/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/user/info/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/user/background/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/user/backgrounds/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/post/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/post/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/comment/posts/**");
			authorize.requestMatchers(HttpMethod.GET, "api/posts/**");
			authorize.requestMatchers(HttpMethod.POST, "/api/post/");
			authorize.requestMatchers(HttpMethod.GET, "/api/comment/post/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/like/**");
			authorize.requestMatchers(HttpMethod.POST, "/api/like/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/like/**");

			authorize.anyRequest().authenticated();
		}).httpBasic(Customizer.withDefaults());

		http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));

		http.addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}