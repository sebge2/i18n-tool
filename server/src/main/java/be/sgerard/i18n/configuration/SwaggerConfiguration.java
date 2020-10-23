package be.sgerard.i18n.configuration;

import be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto;
import io.swagger.v3.core.converter.AnnotatedType;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.core.converter.ResolvedSchema;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * Swagger configuration for all the REST API.
 *
 * <a href="https://github.com/springdoc/springdoc-openapi-demos/blob/master/springdoc-openapi-spring-boot-2-webflux">Documentation</a>
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
                        .contact(new Contact().name("Sébastien Gérard").url("www.sgerard.be"))
                        .license(new License().name("Apache 2.0"))
                )
                .components(
                        new Components().addSecuritySchemes("basicScheme", new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("basic"))
                );
    }

    @Bean
    public RouterFunction<ServerResponse> apiRouterFunction() {
        return route(GET("/api"), req ->
                ServerResponse.temporaryRedirect(URI.create("swagger-ui.html")).build());
    }

    @Bean
    public OpenApiCustomiser schemaCustomiser() {
        final ResolvedSchema resolvedSchema = ModelConverters.getInstance()
                .resolveAsResolvedSchema(new AnnotatedType(TranslationsUpdateEventDto.class));

        return openApi -> openApi
                .schema(resolvedSchema.schema.getName(), resolvedSchema.schema);
    }

}
