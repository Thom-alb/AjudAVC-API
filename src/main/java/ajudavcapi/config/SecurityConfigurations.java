package ajudavcapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                // Desativa CSRF, essencial para APIs REST aceitarem requisições de POST, PUT e
                // DELETE
                .csrf(csrf -> csrf.disable())
                // Configura a gestão de sessão para STATELESS (sem guardar sessão no servidor)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(req -> {
                    req.requestMatchers("/grupo/gerenciar/**").hasRole("LEADER"); // Apenas Líderes acessam
                    req.requestMatchers("/grupo/conteudo/**").hasAnyRole("LEADER", "MEMBER"); // Líderes e Membros acessam
                    req.requestMatchers(HttpMethod.POST, "/user").permitAll();
                    req.anyRequest().permitAll();
                })
                .build();
    }
}