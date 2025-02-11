package com.dhia.Upvertise.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig  {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req.requestMatchers(
                                        "/sponsor-offers/sponsorOffersByStatus",
                                        "/sponsor-offers/allSponsorOffers",
                                        "/auth/**",
                                        "/v2/api-docs",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-resources",
                                        "/swagger-resources/**",
                                        "/configuration/ui",
                                        "/configuration/security",
                                        "/swagger-ui/**",
                                        "/webjars/**",
                                        "/swagger-ui.html"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        //auth ->
                        //auth.jwt(token -> token.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
                         );


        return http.build();
    }
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("http://localhost:9090/realms/Upvertise/protocol/openid-connect/certs")
                .build();
    }
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
            var resourceRoles = jwt.getClaimAsMap("resource_access");
            if (resourceRoles != null && resourceRoles.containsKey("Upvertise-rest-api")) {
                var roles = (Map<String, Object>) resourceRoles.get("Upvertise-rest-api");
                var roleList = (Collection<String>) roles.get("roles");
                var grantedAuthorities = roleList.stream()
                        .map(role -> "ROLE_" + role)  // Add ROLE_ prefix
                        .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet());
                authorities.addAll(grantedAuthorities);
            }
            return authorities;
        });

        return converter;
    }
}
