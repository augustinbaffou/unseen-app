package fr.augustinbaffou.unseen.commun.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI unseenOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Unseen API")
                        .description("API REST de l'application Unseen — découverte de bars à Nantes.")
                        .version("0.0.1")
                        .contact(new Contact()
                                .name("Augustin Baffou")
                        )
                );
    }
}
