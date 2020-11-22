package be.sgerard.i18n.service.user.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.user.persistence.ExternalUserEntity;
import be.sgerard.i18n.model.user.persistence.InternalUserEntity;
import be.sgerard.i18n.model.user.persistence.UserEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.user.UserRepository;
import be.sgerard.test.i18n.model.UserEntityAsserter;
import be.sgerard.test.i18n.support.CleanupDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static be.sgerard.test.i18n.model.UserEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
public class UserSnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private UserSnapshotHandler handler;

    @Autowired
    private UserRepository userRepository;

    @TempDir
    public File tempDir;

    private final InternalUserEntity janeDoeUser = janeDoeUser();
    private final ExternalUserEntity garrickKleinUser = garrickKleinUser();

    @BeforeEach
    public void setupUser() {
        StepVerifier.create(userRepository.save(janeDoeUser)).expectNextCount(1).verifyComplete();
        StepVerifier.create(userRepository.save(garrickKleinUser)).expectNextCount(1).verifyComplete();

        this.user.initAdminUser();
    }

    @Test
    @CleanupDatabase
    public void exportCleanValidate() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberUsers(3);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberUsers(0);

        StepVerifier
                .create(handler.validate(tempDir))
                .expectNextMatches(ValidationResult::isSuccessful)
                .verifyComplete();
    }

    @Test
    @CleanupDatabase
    public void exportCleanImport() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberUsers(3);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberUsers(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberUsers(3);

        assertUser(JANE_DOE_ID, janeDoeUser);
        assertUser(GARRICK_KLEIN_ID, garrickKleinUser);
    }

    private void assertNumberUsers(int expected) {
        StepVerifier
                .create(userRepository.findAll().collectList())
                .expectNextMatches(actual -> actual.size() == expected)
                .verifyComplete();
    }

    private void assertUser(String userId, UserEntity expected) {
        StepVerifier.create(userRepository.findById(userId))
                .assertNext(actual ->
                        UserEntityAsserter.newAssertion()
                                .expectEquals(actual, expected)
                )
                .verifyComplete();
    }
}
