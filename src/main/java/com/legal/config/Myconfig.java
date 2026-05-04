package com.legal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableWebSecurity
public class Myconfig {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Bean
	public UserDetailsService userDetailsDervice() {

		return new UserDetailsServiceImpl();
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {

		return new BCryptPasswordEncoder();
	}

	@Bean
	public RestTemplate restTemplate() {

		return new RestTemplate();
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {

		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsDervice());
		authenticationProvider.setPasswordEncoder(passwordEncoder());

		return authenticationProvider;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.authorizeHttpRequests(auth -> {
			auth.requestMatchers("/admin/**").hasRole("ADMIN")
					.requestMatchers("/user/**").hasRole("USER")
					.requestMatchers("/**").permitAll().anyRequest()
					.authenticated();
		}).formLogin(
				login -> login.loginPage("/login").loginProcessingUrl("/dologin").defaultSuccessUrl("/user/index"))
				.sessionManagement(session -> session
						.invalidSessionUrl("/signin"));

		return httpSecurity.build();

		/*
		 * httpSecurity .authorizeHttpRequests()
		 * .requestMatchers("/admin/**").hasRole("ADMIN")
		 * .requestMatchers("/user/**").hasRole("USER")
		 * .requestMatchers("/**").permitAll().anyRequest().authenticated().and().
		 * formLogin().and().csrf().disable();
		 * 
		 * 
		 * 
		 * return httpSecurity.build();
		 */
	}

}
