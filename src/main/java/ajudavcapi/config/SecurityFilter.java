package ajudavcapi.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import ajudavcapi.domain.repository.UserRepository;
import ajudavcapi.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenService.validateToken(token);

                // Garante que o subject retornado pelo JWT é um e-mail válido
                if (subject != null && !subject.isBlank()) {
                    // Busca o usuário de forma segura sem lançar RuntimeException no filtro
                    var userOptional = userRepository.findByEmail(subject);

                    if (userOptional.isPresent()) {
                        UserDetails user = userOptional.get();
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        // Define a autenticação no contexto do Spring Security
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
                // Caso o token esteja expirado, corrompido ou o usuário não exista mais,
                // limpa o contexto para garantir segurança sem quebrar a execução do servidor
                SecurityContextHolder.clearContext();
                logger.warn("Falha ao autenticar token JWT: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        String token = authHeader.replace("Bearer ", "").trim();

        // Trata envios acidentais de strings vazias ou palavras reservadas do JS
        if (token.isEmpty() || token.equalsIgnoreCase("null") || token.equalsIgnoreCase("undefined")) {
            return null;
        }

        return token;
    }
}