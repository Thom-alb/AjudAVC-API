package ajudavcapi.service;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.api_client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api_client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api_client.http.javanet.NetHttpTransport;
import com.google.api_client.json.gson.GsonFactory;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.domain.repository.UserRepository;

@Service
public class GoogleAuthService {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public GoogleAuthService(UserRepository userRepository, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticateWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Token do Google inválido ou expirado.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Busca usuário pelo e-mail ou cadastra se for o primeiro acesso via Google
            UserEntity user = userRepository.findByEmail(email)
                    .orElseGet(() -> createGoogleUser(email, name));

            return tokenService.generateToken(user);

        } catch (Exception e) {
            throw new IllegalArgumentException("Falha na autenticação com o Google: " + e.getMessage());
        }
    }

    private UserEntity createGoogleUser(String email, String name) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setName(name);
        // Gera uma senha aleatória para atender à constraint NotNull da entidade
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        return userRepository.save(user);
    }
}