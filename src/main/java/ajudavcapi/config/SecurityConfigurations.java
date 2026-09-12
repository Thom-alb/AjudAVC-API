package ajudavcapi.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    
    @Autowired
    private SecurityFilter securityFilter;

    @Bean 
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desativa CSRF (necessário para APIs REST com JWT)
                .csrf(csrf -> csrf.disable())
                
                // Conecta explicitamente o Bean de configuração de CORS criado abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configuração de sessão STATELESS
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                
                .authorizeHttpRequests(req -> {
                    // Libera requisições de PREFLIGHT (OPTIONS) do navegador/CORS
                    req.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll();
                    
                    // Rotas públicas de Autenticação
                    req.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/register").permitAll();
                    req.requestMatchers(HttpMethod.POST, "/auth/google").permitAll();
                    
                    // Qualquer outra requisição
                    req.anyRequest().permitAll(); 
                })
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Aceita qualquer origem (localhost:8081, Expo Go, Render, etc.)
        configuration.setAllowedOriginPatterns(List.of("*"));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        
        // Headers e Credenciais
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}