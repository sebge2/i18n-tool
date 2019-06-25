package be.sgerard.poc.githuboauth.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Sebastien Gerard
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class MainConfiguration {
}
