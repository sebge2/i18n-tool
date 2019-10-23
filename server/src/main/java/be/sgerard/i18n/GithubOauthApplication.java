package be.sgerard.i18n;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories
@EnableScheduling
@EnableJdbcHttpSession
public class GithubOauthApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubOauthApplication.class, args);
    }

}
