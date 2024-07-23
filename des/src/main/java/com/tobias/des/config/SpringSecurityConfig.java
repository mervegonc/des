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

			authorize.requestMatchers("/api/auth/signin").permitAll().requestMatchers("/api/auth/forgotpassword")
					.permitAll().requestMatchers("/api/auth/signup").permitAll();
			authorize.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
			authorize.requestMatchers(HttpMethod.GET, "/api/user/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/post/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/comment/posts/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/like/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/article/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/articlelike/**");
			authorize.requestMatchers(HttpMethod.GET, "/api/articlecomment/**");

			authorize.requestMatchers(HttpMethod.POST, "/api/article/create");
			authorize.requestMatchers(HttpMethod.POST, "/api/like/**");
			authorize.requestMatchers(HttpMethod.POST, "/api/articlelike/**");
			authorize.requestMatchers(HttpMethod.POST, "/api/articlecomment/**");
			authorize.requestMatchers(HttpMethod.POST, "/api/post/**");

			authorize.requestMatchers(HttpMethod.PUT, "/api/post/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/user/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/comment/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/article/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/articlelike/**");
			authorize.requestMatchers(HttpMethod.PUT, "/api/articlecomment/**");

			authorize.requestMatchers(HttpMethod.DELETE, "/api/like/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/user/connections/delete/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/post/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/comment/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/article/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/articlelike/**");
			authorize.requestMatchers(HttpMethod.DELETE, "/api/articlecomment/**");
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