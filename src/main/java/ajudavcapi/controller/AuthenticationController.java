
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
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
import ajudavcapi.domain.dto.auth.TokenResponseDTO;
import ajudavcapi.domain.dto.auth.GoogleLoginDTO;
import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.TokenService;
import ajudavcapi.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Collections;

@RestController
@Tag(name = "Autenticação", description = "Autenticação local, cadastro e login com Google.")
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Operation(summary = "Autenticar com e-mail e senha", description = "Valida as credenciais e retorna um JWT para uso no cabeçalho Authorization: Bearer. Não exige JWT.", security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para AuthenticationDTO", required = true) @RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserEntity) auth.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @Operation(summary = "Cadastrar conta", description = "Cria uma conta local e retorna o usuário cadastrado. Não exige JWT.", security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso; confira o cabeçalho Location"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Grupo ou recurso relacionado não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito, como e-mail ou associação duplicada")
        })
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UserRequestDTO", required = true) @RequestBody @Valid UserRequestDTO data,
            UriComponentsBuilder uriBuilder) {

        try {
            UserEntity novoUsuario = userService.adicionarUsuario(data);

            URI uri = uriBuilder
                    .path("/user/{id}")
                    .buildAndExpand(novoUsuario.getId())
                    .toUri();

            return ResponseEntity
                    .created(uri)
                    .body(new UserResponseDTO(novoUsuario));

        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(e.getMessage());
        }
    }

    @Operation(summary = "Autenticar com Google", description = "Valida o ID token emitido pelo Google e retorna um JWT da aplicação. Não exige JWT.", security = {})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PostMapping("/google")
    public ResponseEntity<?> loginWithGoogle(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para GoogleLoginDTO", required = true) @RequestBody @Valid GoogleLoginDTO data) {
        try {
            // 1. Valida o ID Token recebido do React Native com o Google
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

            // 2. Busca o usuário por e-mail ou cria um novo usando os métodos reais do UserService
            UserEntity usuario = userService.buscarPorEmail(email)
                .orElseGet(() -> {
                    // Passamos uma senha vazia para indicar ao UserService que é uma conta do Google
                    UserRequestDTO novoUserDTO = new UserRequestDTO(name, email, ""); 
                    return userService.adicionarUsuario(novoUserDTO);
                });

            // 3. Reutiliza o seu TokenService para gerar o JWT da aplicação
            String token = tokenService.generateToken(usuario);

            // 4. Retorna no mesmo formato estruturado de resposta que o seu login tradicional
            return ResponseEntity.ok(new TokenResponseDTO(token));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro na autenticação com o Google.");
        }
    }
}
