package be.sgerard.poc.githuboauth.configuration;

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

import static java.util.Collections.emptyList;

/**
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
                .apis(RequestHandlerSelectors.basePackage("be.sgerard.poc.githuboauth.controller"))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "REST API",
                "",
                "",
                "Terms of service",
                new Contact("Sébastien Gérard", "www.emasphere.com", "sgerard@emasphere.com"),
                null,
                null,
                emptyList()
        );
    }
}
