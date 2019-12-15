package be.sgerard.i18n.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.time.Instant;
import java.util.Optional;

import static java.util.Collections.emptyList;

/**
 * Swagger configuration for all the REST API.
 *
 * @author Sebastien Gerard
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .directModelSubstitute(Instant.class, java.util.Date.class)
                .select()
                .apis(RequestHandlerSelectors.basePackage("be.sgerard.i18n.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .genericModelSubstitutes(Optional.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "REST API",
                "Web API of the i18n tool",
                "1.0",
                "Terms of service",
                new Contact("Sébastien Gérard", "www.sgerard.be", ""),
                null,
                null,
                emptyList()
        );
    }
}
