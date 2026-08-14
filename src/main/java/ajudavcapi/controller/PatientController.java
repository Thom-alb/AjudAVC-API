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

import ajudavcapi.domain.dto.patient.CreatePatientDTO;
import ajudavcapi.domain.dto.patient.PatientResponseDTO;
import ajudavcapi.domain.entity.UserEntity;
import ajudavcapi.service.PatientService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Cadastrar Paciente (Somente LÍDER)
    @PostMapping
    @PreAuthorize("hasRole('LEADER')")
    public ResponseEntity<PatientResponseDTO> createPatient(
            @RequestBody @Valid CreatePatientDTO dto,
            @AuthenticationPrincipal UserEntity userLogado,
            UriComponentsBuilder uriBuilder) {

        PatientResponseDTO response = patientService.createPatient(dto, userLogado);

        URI uri = uriBuilder.path("/patients/{id}").buildAndExpand(response.id()).toUri();
        return ResponseEntity.created(uri).body(response);
    }

    // Obter Paciente do Grupo (Líder ou Membro)
    @GetMapping("/me")
    public ResponseEntity<PatientResponseDTO> getMyPatient(
            @AuthenticationPrincipal UserEntity userLogado) {

        PatientResponseDTO response = patientService.getPatientByGroup(userLogado);
        return ResponseEntity.ok(response);
    }
}
