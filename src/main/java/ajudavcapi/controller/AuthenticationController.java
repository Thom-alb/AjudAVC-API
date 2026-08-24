package ajudavcapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.auth.AuthenticationDTO;
import ajudavcapi.domain.dto.auth.TokenResponseDTO;
import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.TokenService;
import ajudavcapi.service.UserService;
import jakarta.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserEntity) auth.getPrincipal());
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@RequestBody @Valid UserRequestDTO data, UriComponentsBuilder uriBuilder) {
        UserEntity novoUsuario = userService.adicionarUsuario(data);
        
        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserResponseDTO(novoUsuario));
    }
    
}
