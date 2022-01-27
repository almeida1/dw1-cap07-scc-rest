package com.fatec.scc.security;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true) //enables Spring Security pre/post annotations
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	@Autowired
	UserDetailsService userDetailsService; //injeta a implementacao UserDetailsServiceImpl
	@Autowired
	private JWTUtil jwtUtil;
	//recursos com acesso liberado deve-se estabelecer a politica para cadastros de usuario
	private static final String[] PUBLIC_MATCHERS = {
			"/h2-console/**",
			"/users/sign-up/**",
			"/api/v1/clientes/swagger-ui/**",
			"/swagger-ui/**"
	};
	//recursos de consulta do tipo GET com acesso liberado
	private static final String[] PUBLIC_MATCHERS_GET = {
			"/api/v1/clientes"
			//"/swagger-ui/index.html/**"
			//"/swagger-ui/**"
	};
    @Override
    protected void configure(HttpSecurity http) throws Exception {
    	//http.headers().frameOptions().disable();//libera o acesso do H2
    	http.cors().and().csrf().disable();
        http.authorizeRequests()
        		.antMatchers(HttpMethod.GET,PUBLIC_MATCHERS_GET).permitAll()
        		.antMatchers(PUBLIC_MATCHERS).permitAll()
                .anyRequest().authenticated(); //outras requisicoes autenticadas
             
        http.addFilter(new JWTAuthenticationFilter(authenticationManager(), jwtUtil));
		http.addFilter(new JWTAuthorizationFilter(authenticationManager(), jwtUtil, userDetailsService));
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
     
    }
    @Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(pc());
	}
	public BCryptPasswordEncoder pc() {
		return new BCryptPasswordEncoder();
	}
    @Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration().applyPermitDefaultValues();
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		
		return source;
	}
    @Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/static/**", "/css/**", "/js/**", "/images/**", "/h2-console/**","/api/v1/clientes/**" ,"/api/v1/clientes/v3.0.0/swagger-ui/**", "/swagger-ui/**");
	}
  }