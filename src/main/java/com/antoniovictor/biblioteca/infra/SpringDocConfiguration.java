package com.antoniovictor.biblioteca.infra;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                        .addSecuritySchemes("basic-auth",
                                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic")))
                .info(new Info().title("Biblioteca API").version("0.0.1")
                        .description("API de um sistema de biblioteca, onde é possível cadastrar, buscar, atualizar e deletar livros e usuários. Também é possível reservar livros e pegar emprestado. A API também conta com um sistema de notificações que avisa para o usuário via email quando um livro reservado está disponível, quando o vencimento do empréstimo está próximo e quando já venceu. Também contém um sistema de autenticação e autorização via token JWT, sendo possível fazer requisições somente com usuários autenticados e alguns enpoints somente se o usuário tiver determinada role."));
    }
}
