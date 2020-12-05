package be.sgerard.i18n.service.i18n.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.test.i18n.model.BundleKeyEntityAsserter;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static be.sgerard.test.i18n.model.BundleKeyEntityTestUtils.*;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitHubRepositoryCreationDto;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.I18N_TOOL_GITHUB_ACCESS_TOKEN;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BundleKeyEntitySnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private BundleKeyEntitySnapshotHandler handler;

    @Autowired
    private BundleKeyEntityRepository repository;

    @TempDir
    public File tempDir;

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .onCurrentGitProject()
                .start();
    }

    @BeforeEach
    public void setupTranslations() {
        StepVerifier.create(repository.save(accessDeniedDevelopGitHub())).expectNextCount(1).verifyComplete();
        StepVerifier.create(repository.save(workspacesTitleDevelopGitHub())).expectNextCount(1).verifyComplete();
    }

    @AfterAll
    public void destroy() {
        remoteRepository.stopAll();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanValidate() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberBundleKeys(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberBundleKeys(0);

        StepVerifier
                .create(handler.validate(tempDir))
                .expectNextMatches(ValidationResult::isSuccessful)
                .verifyComplete();
    }

    @Test
    @CleanupDatabase
    @WithJaneDoeAdminUser
    public void exportCleanImport() {
        StepVerifier
                .create(handler.exportAll(tempDir))
                .verifyComplete();

        assertNumberBundleKeys(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberBundleKeys(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberBundleKeys(2);

        assertBundleKey(DEVELOP_ACCESS_DENIED_EXCEPTION, accessDeniedDevelopGitHub());
        assertBundleKey(DEVELOP_WORKSPACES_TITLE, workspacesTitleDevelopGitHub());
    }

    private void assertNumberBundleKeys(int expected) {
        StepVerifier
                .create(repository.findAll().collectList())
                .expectNextMatches(actual -> actual.size() == expected)
                .verifyComplete();
    }

    private void assertBundleKey(String bundleKeyId, BundleKeyEntity expected) {
        StepVerifier.create(repository.findById(bundleKeyId))
                .assertNext(actual ->
                        BundleKeyEntityAsserter.newAssertion()
                                .expectEquals(actual, expected)
                )
                .verifyComplete();
    }
}
