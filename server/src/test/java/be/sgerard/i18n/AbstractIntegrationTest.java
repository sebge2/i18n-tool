package be.sgerard.i18n;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest(classes = {GithubOauthApplication.class, TestConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
@Tag("IntegrationTest")
public abstract class AbstractIntegrationTest {

}
