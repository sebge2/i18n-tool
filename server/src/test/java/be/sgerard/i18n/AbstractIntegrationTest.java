package be.sgerard.i18n;

import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GithubOauthApplication.class, TestConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

}
