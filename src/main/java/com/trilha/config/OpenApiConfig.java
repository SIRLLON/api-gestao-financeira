package com.trilha.config;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springdoc.core.GroupedOpenApi;

@Configuration
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .packagesToScan("com.trilha") // Ajuste para o pacote correto onde estão seus controladores
                .addOpenApiCustomiser(openApi -> openApi.info(new Info()
                        .title("API de Gestão Financeira")
                        .version("1.0")
                        .description("Documentação dos endpoints da API de gestão financeira")
                        .contact(new Contact()
                                .name("Sirllon Cruz")
                                .url("https://www.linkedin.com/in/sirllon-cruz/")
                        )
                ))
                .build();
    }
}
