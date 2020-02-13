package com.dataguise.saas;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@Profile("dev")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    DgControllerAuthProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable().authorizeRequests()
            .antMatchers("/").permitAll()
            .antMatchers("/assets/**", "/*.js", "/*.css", "/*.svg").permitAll()
            .antMatchers("/api/about/").permitAll()
            .antMatchers("/login").permitAll()
                .antMatchers("/loginSSO").permitAll()
            .antMatchers("/metrics","/health","/prometheus").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(new JWTLoginFilter("/api/login", authenticationManager()),
                    UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JWTLoginFilterSSO("/api/loginSSO", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).cors();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(new ArrayList<String>(Arrays.asList("*")));
        configuration.setAllowedMethods(new ArrayList<String>(Arrays.asList("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH")));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(new ArrayList<String>(Arrays.asList("Authorization", "Cache-Control", "Content-Type")));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
