
package ajudavcapi.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.dto.patient.UpdatePatientDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.PatientService;
import jakarta.validation.Valid;

@RestController
@Tag(name = "Pacientes", description = "Cadastro e consulta de informações do paciente vinculado ao grupo.")
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @Operation(summary = "Cadastrar paciente", description = "Cria o paciente do grupo. A implementação atual restringe a rota ao papel global LEADER; confirme que esse papel é sincronizado com o papel de líder no grupo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso; confira o cabeçalho Location"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Grupo ou recurso relacionado não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito, como e-mail ou associação duplicada")
        })
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para CreatePatientDTO", required = true) @RequestBody @Valid CreatePatientDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        PatientResponseDTO response = patientService.createPatient(dto, userLogado);

        URI uri = uriBuilder
                .path("/patients/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(uri).body(response);
    }

    @Operation(summary = "Consultar paciente do grupo", description = "Retorna o paciente vinculado ao grupo do usuário autenticado.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operação concluída com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros inválidos ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para acessar o recurso"),
            @ApiResponse(responseCode = "404", description = "Recurso ou vínculo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @GetMapping("/me")
    public ResponseEntity<PatientResponseDTO> getMyPatient(
            @AuthenticationPrincipal UserEntity userLogado) {

        PatientResponseDTO response = patientService.getPatientByGroup(userLogado);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Atualizar paciente", description = "Atualiza dados do paciente, após validar vínculo e permissões do usuário no grupo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Recurso atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Payload inválido ou regra de negócio violada"),
            @ApiResponse(responseCode = "401", description = "Token JWT ausente, inválido ou expirado"),
            @ApiResponse(responseCode = "403", description = "Usuário sem permissão para executar a operação"),
            @ApiResponse(responseCode = "404", description = "Recurso não encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflito com o estado atual do recurso")
        })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @Parameter(description = "Identificador do recurso na API", example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Payload validado para UpdatePatientDTO", required = true) @RequestBody @Valid UpdatePatientDTO dto,
            @AuthenticationPrincipal UserEntity userLogado) {

        PatientResponseDTO response = patientService.updatePatient(id, dto, userLogado);

        return ResponseEntity.ok(response);
    }
}
