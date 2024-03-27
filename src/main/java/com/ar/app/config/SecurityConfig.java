package com.ar.app.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final JwtAuthenticationFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;
	
	@Value("${cors.set.allowed.origins}")
	private String[] CROSS_ORIGIN_URLS;
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
        .cors(Customizer.withDefaults())
		.csrf(csrf -> csrf.disable())
		.formLogin(formLogin -> formLogin.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .exceptionHandling((exceptionHandling) ->
			exceptionHandling
				.authenticationEntryPoint(new RestAuthenticationEntryPoint())
				//.accessDeniedPage("/errors/access-denied")
		)
		.authorizeHttpRequests(ahr ->
			ahr.requestMatchers(
					"/api/v1/auth/**",
					"/ws/**"
			).permitAll()
			.anyRequest().authenticated()
		)
		.sessionManagement(sm -> 
		     sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	    )
		.authenticationProvider(authenticationProvider)
		.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
		.logout((logout) -> logout
			.logoutUrl("/api/v1/auth/logout")
			.logoutSuccessHandler((request, response, authentication) -> 
				SecurityContextHolder.clearContext()
			)
 		)
		;

    return http.build();
	}
    
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.setAllowedOrigins(Arrays.asList(CROSS_ORIGIN_URLS));
		corsConfiguration.setAllowedHeaders(Arrays.asList(
					"Origin","Access-Control-Allow-Origin", "Content-Type",
					"Accept","Authorization","Origin, Accept","X-Requested-With",
					"Access-Control-Request-Method","Access-Control-Request-Headers"
				));
		corsConfiguration.setExposedHeaders(Arrays.asList(
					"Origin","Content-Type","Accept","Authorization",
					"Access-Control-Allow-Origin","Access-Control-Allow-Origin",
					"Access-Control-Allow-Credentials"
				));
		corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
		UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", corsConfiguration);
		return new CorsFilter(urlBasedCorsConfigurationSource);
	}

}

