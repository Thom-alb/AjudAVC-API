package ajudavcapi.service;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import ajudavcapi.domain.enums.AuthProvider;
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

    @Transactional
    public String authenticateWithGoogle(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Token do Google inválido, adulterado ou expirado.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            // Busca usuário existente pelo e-mail ou registra um novo
            UserEntity user = userRepository.findByEmail(email)
                    .map(existingUser -> {
                        // Se o usuário foi criado originalmente via LOCAL, podemos vincular o provedor GOOGLE
                        if (existingUser.getAuthProvider() == null) {
                            existingUser.setAuthProvider(AuthProvider.GOOGLE);
                            return userRepository.save(existingUser);
                        }
                        return existingUser;
                    })
                    .orElseGet(() -> createGoogleUser(email, name));

            // Gera e retorna o JWT da SUA API
            return tokenService.generateToken(user);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("Falha na validação do token com o Google: " + e.getMessage(), e);
        }
    }

    private UserEntity createGoogleUser(String email, String name) {
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setName(name);
        user.setAuthProvider(AuthProvider.GOOGLE);
        
        // Gera hash de senha aleatória segura para satisfazer a restrição NotNull do banco
        user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        
        return userRepository.save(user);
    }
}