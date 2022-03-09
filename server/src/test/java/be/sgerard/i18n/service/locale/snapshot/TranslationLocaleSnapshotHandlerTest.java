package be.sgerard.i18n.service.locale.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.test.i18n.model.TranslationLocaleEntityAsserter;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class TranslationLocaleSnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private TranslationLocaleSnapshotHandler handler;

    @Autowired
    private TranslationLocaleRepository localeRepository;

    @TempDir
    public File tempDir;

    private final TranslationLocaleEntity english = enLocale();
    private final TranslationLocaleEntity wallon = frBeWallonLocale();

    @BeforeEach
    public void setupUser() {
        StepVerifier.create(localeRepository.save(english)).expectNextCount(1).verifyComplete();
        StepVerifier.create(localeRepository.save(wallon)).expectNextCount(1).verifyComplete();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanValidate() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberLocales(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberLocales(0);

        StepVerifier
                .create(handler.validate(tempDir))
                .expectNextMatches(ValidationResult::isSuccessful)
                .verifyComplete();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanImport() {
        assertNumberLocales(2);

        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberLocales(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberLocales(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberLocales(2);

        assertLocale(ENGLISH_ID, english);
        assertLocale(FRANCAIS_WALLON_ID, frBeWallonLocale());
    }

    private void assertNumberLocales(int expected) {
        assertThat(locale.getLocales()).hasSize(expected);
    }

    private void assertLocale(String localeId, TranslationLocaleEntity expected) {
        StepVerifier.create(localeRepository.findById(localeId))
                .assertNext(actual ->
                        TranslationLocaleEntityAsserter.newAssertion()
                                .expectEquals(actual, expected)
                )
                .verifyComplete();
    }
}
