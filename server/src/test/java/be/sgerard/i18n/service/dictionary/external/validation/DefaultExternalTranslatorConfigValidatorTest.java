package be.sgerard.i18n.service.dictionary.external.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.service.translator.validation.DefaultExternalTranslatorConfigValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DefaultExternalTranslatorConfigValidatorTest {

    @InjectMocks
    private DefaultExternalTranslatorConfigValidator validator;

    @Test
    public void successful() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isTrue())
                .verifyComplete();
    }

    @Test
    public void missingLabel() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();
        config.setLabel(null);

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isFalse())
                .verifyComplete();
    }

    @Test
    public void missingLinkUrl() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();
        config.setLinkUrl(null);

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isFalse())
                .verifyComplete();
    }

    private ExternalTranslatorGenericRestConfigEntity createConfig() {
        return new ExternalTranslatorGenericRestConfigEntity("My source", "acme.com", HttpMethod.GET, "my-url.com", "$.text");
    }
}