package be.sgerard.i18n.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger configuration for all the REST API.
 *
 * @author Sebastien Gerard
 */
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("i18n Tool")
                        .version("1.0")
                        .description("Web API of the i18n tool")
                        .termsOfService("http://swagger.io/terms/")
                        .contact(new Contact().name("Sébastien Gérard").url("www.sgerard.be"))
                        .license(new License().name("Apache 2.0"))
                );
    }

}
