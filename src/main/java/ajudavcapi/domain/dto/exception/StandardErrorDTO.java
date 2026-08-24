package ajudavcapi.domain.dto.exception;

import java.time.Instant;
import java.util.Map;

public record StandardErrorDTO(
    Instant timestamp,
    Integer status,
    String error,
    String message,
    String path,
    Map<String, String> validationErrors // Útil para erros de formulário (@Valid)
) {
    // Construtor utilitário para erros sem mapa de validação
    public StandardErrorDTO(Instant timestamp, Integer status, String error, String message, String path) {
        this(timestamp, status, error, message, path, null);
    }
}