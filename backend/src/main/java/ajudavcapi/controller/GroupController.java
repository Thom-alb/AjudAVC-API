package ajudavcapi.controller;


import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupService;
import ajudavcapi.domain.dto.group.CreateGroupDTO;
import ajudavcapi.domain.dto.group.GroupResponseDTO;
import ajudavcapi.domain.dto.group.JoinGroupDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    // Criar um novo Grupo de Cuidado (Apenas LÍDER)
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<GroupResponseDTO> createGroup(
            @RequestBody @Valid CreateGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        GroupResponseDTO response = groupService.createGroup(dto, userLogado);

        URI uri = uriBuilder.path("/groups/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Entrar em um grupo existente informando o código de convite (Membro / Cuidador)
    @PostMapping("/join")
    public ResponseEntity<GroupResponseDTO> joinGroup(
            @RequestBody @Valid JoinGroupDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.joinGroup(dto, userLogado);
        return ResponseEntity.ok(response);
    }

    // Consultar informações do grupo de cuidado
    @GetMapping("/me")
    public ResponseEntity<GroupResponseDTO> getMyGroup(
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupResponseDTO response = groupService.getMyGroup(userLogado);
        return ResponseEntity.ok(response);
    }
}