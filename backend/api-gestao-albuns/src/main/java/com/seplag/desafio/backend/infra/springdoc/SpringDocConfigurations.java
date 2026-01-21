package com.seplag.desafio.backend.infra.springdoc;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer; // Importante
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfigurations {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement().addList("bearer-key"))
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Desafio Seplag API")
                        .description("API Rest para gestão de álbuns e músicas")
                        .contact(new Contact()
                                .name("Bruno Agnelo")
                                .email("bruno@teste.mt.gov.br"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://desafio.seplag/api/licenca")));
    }

    /*
     * --- A CORREÇÃO DO ERRO DE SORT ---
     * Este Bean ensina o Swagger a limpar os parametros de Sort/Pageable
     * para que eles não sejam enviados como arrays bugados ["nome,asc"].
     */
    @Bean
    public OpenApiCustomizer swaggerSortCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations().forEach(operation -> {
            if (operation.getParameters() != null) {
                operation.getParameters().forEach(parameter -> {
                    if ("sort".equals(parameter.getName())) {
                        // Força o Swagger a entender que 'sort' é uma coleção simples de strings
                        // e não um array complexo que ele tenta serializar com colchetes
                        parameter.getSchema().setType("string");
                    }
                });
            }
        }));
    }
}