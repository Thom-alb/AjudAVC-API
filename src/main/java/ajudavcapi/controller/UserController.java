package ajudavcapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajudavcapi.domain.dto.user.UserResponseDTO;
import ajudavcapi.service.UserService;

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

}
