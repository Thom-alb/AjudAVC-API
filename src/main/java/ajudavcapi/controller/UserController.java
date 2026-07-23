package ajudavcapi.controller;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.user.UserRequestDTO;
import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.UserService;
import jakarta.validation.Valid;

@RequestMapping("/user")
@RestController
public class UserController {
    
    @Autowired
    private UserService service;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getTodos() {
        List<UserResponseDTO> lista = service.listarUsuarios().stream()
            .map(UserResponseDTO::new)
            .toList();

        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> buscarPorId(@PathVariable Long id) {
        UserEntity user = service.buscarPorId(id);
        return ResponseEntity.ok(new UserResponseDTO(user));
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> cadastrar(@RequestBody @Valid UserRequestDTO dto, UriComponentsBuilder uriBuilder) {
        UserEntity novoUsuario = service.adicionarUsuario(dto);
        
        URI uri = uriBuilder.path("/user/{id}").buildAndExpand(novoUsuario.getId()).toUri();
        return ResponseEntity.created(uri).body(new UserResponseDTO(novoUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> atualizar(@PathVariable Long id, @RequestBody @Valid UserRequestDTO dto) {
        UserEntity usuarioAtualizado = service.atualizarUsuario(id, dto);
        return ResponseEntity.ok(new UserResponseDTO(usuarioAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}