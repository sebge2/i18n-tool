package be.sgerard;

import be.sgerard.i18n.service.support.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.Mockito.mock;

@org.springframework.boot.test.context.TestConfiguration
public class TestConfiguration {

    @Bean
    @Primary
    HttpClient httpClient() {
        return mock(HttpClient.class);
    }
}
