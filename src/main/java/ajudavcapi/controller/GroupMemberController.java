package ajudavcapi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupMemberService;
import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/group-members")
public class GroupMemberController {

    @Autowired
    private GroupMemberService groupMemberService;

    // Listar todos os integrantes da rede de apoio do meu grupo
    @GetMapping
    public ResponseEntity<List<GroupMemberResponseDTO>> getGroupMembers(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<GroupMemberResponseDTO> members = groupMemberService.getGroupMembers(userLogado);
        return ResponseEntity.ok(members);
    }

    // Alterar o papel/permissão de um membro (Ex: promover membro a LÍDER)
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<GroupMemberResponseDTO> updateMemberRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateGroupMemberRoleDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupMemberResponseDTO response = groupMemberService.updateMemberRole(id, dto, userLogado);
        return ResponseEntity.ok(response);
    }

    // Remover um membro da rede de apoio (Somente LÍDER)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        groupMemberService.removeMember(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}