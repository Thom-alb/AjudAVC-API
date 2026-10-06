package ajudavcapi.config;

import java.io.IOException;

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
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        var token = this.recoverToken(request);

        if (token != null) {
            try {
                var subject = tokenService.validateToken(token);

                if (subject != null && !subject.isBlank()) {
                    var userOptional = userRepository.findByEmail(subject);

                    if (userOptional.isPresent()) {
                        UserDetails user = userOptional.get();
                        var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            } catch (Exception e) {
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

        if (token.isEmpty() || token.equalsIgnoreCase("null") || token.equalsIgnoreCase("undefined")) {
            return null;
        }

        return token;
    }
}