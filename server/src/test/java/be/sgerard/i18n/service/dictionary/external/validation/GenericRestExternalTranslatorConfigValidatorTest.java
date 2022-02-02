package be.sgerard.i18n.service.dictionary.external.validation;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;
import be.sgerard.i18n.service.translator.validation.GenericRestExternalTranslatorConfigValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GenericRestExternalTranslatorConfigValidatorTest {

    @InjectMocks
    private GenericRestExternalTranslatorConfigValidator validator;

    @Test
    public void successful() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isTrue())
                .verifyComplete();
    }

    @Test
    public void missingMethod() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();
        config.setMethod(null);

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isFalse())
                .verifyComplete();
    }

    @Test
    public void missingUrl() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();
        config.setUrl(null);

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isFalse())
                .verifyComplete();
    }

    @Test
    public void missingQueryExtractor() {
        final ExternalTranslatorGenericRestConfigEntity config = createConfig();
        config.setQueryExtractor(null);

        StepVerifier
                .create(validator.beforePersistOrUpdate(config))
                .assertNext(actual -> assertThat(actual.isSuccessful()).isFalse())
                .verifyComplete();
    }

    private ExternalTranslatorGenericRestConfigEntity createConfig() {
        return new ExternalTranslatorGenericRestConfigEntity("My source", "acme.com", HttpMethod.GET, "my-url.com", "$.text");
    }

}