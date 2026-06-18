package com.insurances.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Plataforma Multi‑Aseguradora API")
                        .version("2.0.0")
                        .description("API para gestión de reclamos, pólizas y aseguradoras con autenticación JWT, historial de estados y documentos adjuntos.")
                        .contact(new Contact()
                                .name("Equipo de Desarrollo")
                                .email("soporte@insurances.com"))
                        .license(new License()
                                .name("Académico")
                                .url("https://github.com/RM24005-JB/API_Incidencias_Aseguradora")));
    }
}