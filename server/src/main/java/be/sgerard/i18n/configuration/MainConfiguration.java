package be.sgerard.i18n.configuration;

import org.springframework.boot.autoconfigure.session.JdbcSessionDataSourceInitializer;
import org.springframework.boot.autoconfigure.session.JdbcSessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.sql.DataSource;

/**
 * @author Sebastien Gerard
 */
@Configuration
@EnableConfigurationProperties({AppProperties.class, JdbcSessionProperties.class /* TODO should be automatically created*/})
public class MainConfiguration {

    public MainConfiguration() {
    }

    @Bean
    public PasswordEncoder externalUserPasswordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean // TODO should be automatically created
    public JdbcSessionDataSourceInitializer jdbcSessionDataSourceInitializer(DataSource dataSource,
                                                                             ResourceLoader resourceLoader,
                                                                             JdbcSessionProperties properties) {
        return new JdbcSessionDataSourceInitializer(dataSource, resourceLoader, properties);
    }
}
