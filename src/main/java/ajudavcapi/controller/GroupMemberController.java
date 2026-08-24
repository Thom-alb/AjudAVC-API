package ajudavcapi.controller;

import java.util.List;

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

import ajudavcapi.domain.dto.groupmember.GroupMemberResponseDTO;
import ajudavcapi.domain.dto.groupmember.UpdateGroupMemberRoleDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.GroupMemberService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/group-members")
public class GroupMemberController {

    private final GroupMemberService groupMemberService;

    // Injeção via Construtor explícita
    public GroupMemberController(GroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    @GetMapping
    public ResponseEntity<List<GroupMemberResponseDTO>> getGroupMembers(
            @AuthenticationPrincipal UserEntity userLogado) {

        List<GroupMemberResponseDTO> members = groupMemberService.getGroupMembers(userLogado);
        return ResponseEntity.ok(members);
    }

    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<GroupMemberResponseDTO> updateMemberRole(
            @PathVariable Long id,
            @RequestBody @Valid UpdateGroupMemberRoleDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        GroupMemberResponseDTO response = groupMemberService.updateMemberRole(id, dto, userLogado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @AuthenticationPrincipal UserEntity userLogado) {

        groupMemberService.removeMember(id, userLogado);
        return ResponseEntity.noContent().build();
    }
}