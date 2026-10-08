package com.recruitment.platform.config;

import com.recruitment.platform.security.JwtAuthenticationFilter;
import com.recruitment.platform.security.JwtTokenProvider;
import com.recruitment.platform.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Classe de configuration principale de Spring Security 6.x.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider tokenProvider;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(tokenProvider, customUserDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Activation des CORS (configuré ci-dessous) et désactivation du CSRF (inutile avec les tokens JWT)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            
            // Pas de session d'état côté serveur (REST stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuration des règles d'accès aux URLs
            .authorizeHttpRequests(auth -> auth
                // Endpoints d'authentification publics
                .requestMatchers("/api/auth/**").permitAll()
                // Fichiers uploadés (photos, CVs) accessibles publiquement
                .requestMatchers("/uploads/**").permitAll()
                // Profil entreprise public
                .requestMatchers(HttpMethod.GET, "/api/companies/**").permitAll()
                // Accès public pour lister ou rechercher des jobs
                .requestMatchers("/api/jobs/recruiter/offers").hasRole("RECRUITER")
                .requestMatchers(HttpMethod.GET, "/api/jobs/**").permitAll()
                // Modification et suppression d'offres par le recruteur
                .requestMatchers(HttpMethod.PUT, "/api/jobs/*").hasRole("RECRUITER")
                .requestMatchers(HttpMethod.DELETE, "/api/jobs/*").hasRole("RECRUITER")
                // Upload du logo entreprise
                .requestMatchers(HttpMethod.POST, "/api/companies/logo/upload").hasRole("RECRUITER")
                // Endpoints candidats nécessitant le rôle CANDIDATE
                .requestMatchers("/api/candidates/**").hasRole("CANDIDATE")
                // Soumettre une candidature nécessite le rôle CANDIDATE
                .requestMatchers(HttpMethod.POST, "/api/applications").hasRole("CANDIDATE")
                .requestMatchers("/api/applications/my-applications").hasRole("CANDIDATE")
                // Gérer les candidatures pour les offres (recruteurs)
                .requestMatchers("/api/applications/job/**").hasRole("RECRUITER")
                .requestMatchers(HttpMethod.PUT, "/api/applications/*/interview").hasRole("RECRUITER")
                .requestMatchers(HttpMethod.PUT, "/api/applications/*/status").hasRole("RECRUITER")
                // Toute autre requête nécessite d'être authentifié
                .anyRequest().authenticated()
            );

        // Ajout de notre filtre JWT avant le filtre standard de Spring Security
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autoriser le port par défaut de React (3000) et de Vite (5173)
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Cache-Control", "X-User-Id"));
        configuration.setExposedHeaders(Collections.singletonList("Authorization"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
