package be.sgerard.i18n.service.dictionary.external.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.translator.dto.ExternalTranslatorGenericRestConfigDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.dictionary.ExternalTranslatorConfigRepository;
import be.sgerard.i18n.service.translator.snapshot.ExternalTranslatorConfigSnapshotHandler;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

class ExternalTranslatorConfigSnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private ExternalTranslatorConfigSnapshotHandler handler;

    @Autowired
    private ExternalTranslatorConfigRepository configRepository;

    @TempDir
    public File tempDir;

    private ExternalTranslatorGenericRestConfigDto googleConfig;
    private ExternalTranslatorGenericRestConfigDto iTranslateConfig;

    @BeforeEach
    public void setupUser() {
        googleConfig = externalTranslator.googleTranslator().createConfig().get();
        iTranslateConfig = externalTranslator.iTranslate().createConfig().get();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanValidate() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberConfigs(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberConfigs(0);

        StepVerifier
                .create(handler.validate(tempDir))
                .expectNextMatches(ValidationResult::isSuccessful)
                .verifyComplete();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanImport() {
        assertNumberConfigs(2);

        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberConfigs(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberConfigs(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberConfigs(2);

       assertConfig(googleConfig);
        assertConfig(iTranslateConfig);
    }

    private void assertNumberConfigs(int expected) {
        assertThat(externalTranslator.getConfigurations()).hasSize(expected);
    }

    private void assertConfig(ExternalTranslatorGenericRestConfigDto config) {
        StepVerifier.create(configRepository.findById(config.getId()))
                .expectNextCount(1)
                .verifyComplete();
    }

}