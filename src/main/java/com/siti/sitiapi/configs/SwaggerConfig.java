package com.siti.sitiapi.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";
        final String roleHeaderName = "Role";

        return new OpenAPI()
                .info(new Info().title("SITI API").version("1.0.0").description("Sistema Integrado de Transportes Intermunicipais"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName)
                        .addList(roleHeaderName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
                        .addSecuritySchemes(roleHeaderName, new SecurityScheme()
                                .name("Role")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)
                                .description("Role do usuário logado (ADMIN, DRIVE, USER)")));
    }
}
