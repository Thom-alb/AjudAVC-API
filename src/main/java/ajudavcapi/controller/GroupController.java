package ajudavcapi.controller;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/groups")
public class GroupController {

    private final GroupService groupService;

    // Injeção via Construtor explícita
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Criar um novo Grupo de Cuidado.
     */
    @PostMapping
    public ResponseEntity<GroupResponseDTO> createGroup(
            @RequestBody @Valid CreateGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        GroupResponseDTO response = groupService.createGroup(dto, userLogado);

        URI uri = uriBuilder.path("/groups/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    /**
     * Entrar em um grupo existente informando o código de convite.
     */
    @PostMapping("/join")
    public ResponseEntity<GroupResponseDTO> joinGroup(
            @RequestBody @Valid JoinGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.joinGroup(dto, userLogado);
        return ResponseEntity.ok(response);
    }

    /**
     * Consultar informações do grupo do usuário logado.
     */
    @GetMapping("/me")
    public ResponseEntity<GroupResponseDTO> getMyGroup(
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.getMyGroup(userLogado);
        
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Consultar grupo por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponseDTO> getGroupById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.getGroupById(id, userLogado);
        return ResponseEntity.ok(response);
    }
}