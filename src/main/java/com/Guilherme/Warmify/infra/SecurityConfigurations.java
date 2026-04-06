package com.Guilherme.Warmify.infra;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@SecurityScheme(name = SecurityConfigurations.SECURITY, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfigurations {

    @Autowired
    SecurityFilter securityFilter;

    public static final String SECURITY = "bearerAuth";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        //Api-docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // Authentication
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // User management
                        .requestMatchers("/manager/**").permitAll()

                        // Domains
                        .requestMatchers(HttpMethod.GET, "/domain/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/domain/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/domain/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/domain/**").hasRole("ADMIN")

                        // Facebook / Instagram legacy controllers
                        .requestMatchers(HttpMethod.GET, "/facebook/**", "/instagram/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/facebook/**", "/instagram/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/facebook/**", "/instagram/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/facebook/**", "/instagram/**").hasRole("ADMIN")

                        // Business portfolio
                        .requestMatchers(HttpMethod.GET, "/business-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/business-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/business-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/business-portfolios/**").hasRole("ADMIN")

                        // Facebook pages
                        .requestMatchers(HttpMethod.GET, "/facebook-pages/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/facebook-pages/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/facebook-pages/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/facebook-pages/**").hasRole("ADMIN")

                        // Number portfolios
                        .requestMatchers(HttpMethod.GET, "/number-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/number-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/number-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/number-portfolios/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/number-portfolios/**").hasRole("ADMIN")

                        // Recovery keys
                        .requestMatchers(HttpMethod.GET, "/facebook-recovery-keys/**", "/instagram-recovery-keys/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/facebook-recovery-keys/**", "/instagram-recovery-keys/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/facebook-recovery-keys/**", "/instagram-recovery-keys/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/instagram-recovery-keys/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/facebook-recovery-keys/**", "/instagram-recovery-keys/**").hasRole("ADMIN")


                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}