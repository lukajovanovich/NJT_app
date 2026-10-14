package projekat.config;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import projekat.security.JwtAuthenticationFilter;
import projekat.security.SecurityRoles;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/clan-organizacije/login", "/api/clan-organizacije").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/projekat",
                                "/api/projekat/*",
                                "/api/tim",
                                "/api/tim/*",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/projekat", "/api/tim").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/projekat", "/api/tim").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.DELETE, "/api/projekat/*", "/api/tim/*").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers("/api/prijava/pending", "/api/prijava/*/status").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/clan-organizacije/rang-lista").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/clan-organizacije", "/api/clan-organizacije/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.RUKOVODILAC)
                        .requestMatchers(HttpMethod.PUT, "/api/clan-organizacije").hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.RUKOVODILAC)
                        .requestMatchers(HttpMethod.DELETE, "/api/clan-organizacije/*").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers("/api/clanTim/povezi").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers("/api/zadatak/rukovodilac/**").hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.RUKOVODILAC)
                        .requestMatchers("/api/zadatak/my/**", "/api/prijava/**", "/api/clanTim/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
