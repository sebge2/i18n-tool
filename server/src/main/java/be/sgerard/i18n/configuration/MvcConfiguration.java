package be.sgerard.i18n.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Sebastien Gerard
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {

    public MvcConfiguration() {
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
//        registry.addRedirectViewController("/api/doc/v2/api-docs", "/v2/api-docs");
//        registry.addRedirectViewController("/api/doc/configuration/ui", "/configuration/ui");
//        registry.addRedirectViewController("/api/doc/configuration/security", "/configuration/security");
//        registry.addRedirectViewController("/api/doc/swagger-resources", "/swagger-resources");
//        registry.addRedirectViewController("/api/doc", "/api/doc/swagger-ui.html");
//        registry.addRedirectViewController("/api/doc/index.html", "/api/doc/swagger-ui.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/api/doc/swagger-ui.html**").addResourceLocations("classpath:/META-INF/resources/swagger-ui.html");
//        registry.addResourceHandler("/api/doc/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}
