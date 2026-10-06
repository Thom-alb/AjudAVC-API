package ajudavcapi.controller;

import java.net.URI;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import ajudavcapi.domain.dto.auth.AuthenticationDTO;
import ajudavcapi.domain.dto.auth.GoogleLoginDTO;
import ajudavcapi.domain.dto.auth.TokenResponseDTO;
import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.TokenService;
import ajudavcapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserEntity) auth.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO data,
            UriComponentsBuilder uriBuilder) {
        UserEntity novoUsuario = userService.adicionarUsuario(data);

        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserResponseDTO(novoUsuario));
    }

    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@RequestBody @Valid GoogleLoginDTO data) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();

            GoogleIdToken idToken = verifier.verify(data.idToken());
            
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido do Google.");
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");

            UserEntity usuario = userService.buscarPorEmail(email)
                .orElseGet(() -> {
                    UserRequestDTO novoUserDTO = new UserRequestDTO(name, email, ""); 
                    return userService.adicionarUsuario(novoUserDTO);
                });

            String token = tokenService.generateToken(usuario);

            return ResponseEntity.ok(new TokenResponseDTO(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na autenticação com o Google.");
        }
    }
}