package ajudavcapi.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "AjudAVC API",
        version = "v1",
        description = "API de apoio a cuidadores e acompanhamento de pacientes pós-AVC. Recursos são isolados pelo grupo de cuidado do usuário autenticado.",
        contact = @Contact(name = "Equipe AjudAVC")
    ),
    security = @SecurityRequirement(name = "bearer-key"),
    tags = {
        @Tag(name = "Autenticação", description = "Cadastro e autenticação de usuários"),
        @Tag(name = "Grupos de Cuidado", description = "Grupos e isolamento multi-tenant"),
        @Tag(name = "Pacientes", description = "Dados do paciente vinculado ao grupo"),
        @Tag(name = "Atividades e Diário", description = "Atividades, registros e auditoria"),
        @Tag(name = "Agenda e Calendário", description = "Eventos e tarefas de cuidado"),
        @Tag(name = "Membros da Rede", description = "Papéis e permissões de membros"),
        @Tag(name = "Progresso Semanal", description = "Registros de evolução semanal"),
        @Tag(name = "Resumos Mensais", description = "Indicadores mensais agregados"),
        @Tag(name = "Gestão de Usuários", description = "Cadastro e manutenção de usuários")
    }
)
@SecurityScheme(
    name = "bearer-key",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Informe somente o JWT. O Swagger adiciona o prefixo Bearer automaticamente."
)
public class OpenApiConfig {

    @Bean
    public OpenAPI ajudavcOpenAPI() {
        return new OpenAPI()
            .info(new io.swagger.v3.oas.models.info.Info()
                .title("AjudAVC API")
                .version("v1")
                .description("API de apoio a cuidadores e acompanhamento de pacientes pós-AVC."))
            .components(new Components().addSecuritySchemes("bearer-key",
                new io.swagger.v3.oas.models.security.SecurityScheme()
                    .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Informe somente o JWT; o Swagger inclui Bearer.")))
            .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement().addList("bearer-key"));
    }
}
