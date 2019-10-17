package be.sgerard.i18n.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author Sebastien Gerard
 */
@Configuration
@EnableConfigurationProperties(AppProperties.class)
public class MainConfiguration {

    public MainConfiguration() {
    }

    @Bean
    public PasswordEncoder externalUserPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }
}
