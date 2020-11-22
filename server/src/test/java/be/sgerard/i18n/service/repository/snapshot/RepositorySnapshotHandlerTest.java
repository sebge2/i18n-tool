package be.sgerard.i18n.service.repository.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.GitRepositoryEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import be.sgerard.test.i18n.model.RepositoryEntityAsserter;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitHubRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitRepositoryCreationDto;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositorySnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private RepositorySnapshotHandler handler;

    @Autowired
    private RepositoryEntityRepository repository;

    @TempDir
    public File tempDir;

    private final GitHubRepositoryEntity gitHubRepository = i18nToolGitHubRepository();
    private final GitRepositoryEntity gitRepository = i18nToolGitRepository();

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .onCurrentGitProject()
                .start();

        remoteRepository
                .git()
                .create(i18nToolGitRepositoryCreationDto(), "myGitRepo")
                .addUser(I18N_TOOL_GIT_REPO_USER, I18N_TOOL_GIT_REPO_USER_PASSWORD)
                .onCurrentGitProject()
                .start();

        remoteRepository.gitHub().forHint("myGitHubRepo").manageRemoteBranches().createBranches("develop");
        remoteRepository.git().forHint("myGitRepo").manageRemoteBranches().createBranches("develop");
    }

    @BeforeEach
    public void setupRepositories() {
        StepVerifier.create(repository.save(gitHubRepository)).expectNextCount(1).verifyComplete();
        StepVerifier.create(repository.save(gitRepository)).expectNextCount(1).verifyComplete();
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

        assertNumberRepositories(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberRepositories(0);

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

        assertNumberRepositories(2);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberRepositories(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberRepositories(2);

        assertRepository(I18N_TOOL_GITHUB_ID, gitHubRepository);
        assertRepository(I18N_TOOL_GIT_ID, gitRepository);
    }

    private void assertNumberRepositories(int expected) {
        StepVerifier
                .create(repository.findAll().collectList())
                .expectNextMatches(actual -> actual.size() == expected)
                .verifyComplete();
    }

    private void assertRepository(String userId, RepositoryEntity expected) {
        StepVerifier.create(repository.findById(userId))
                .assertNext(actual ->
                        RepositoryEntityAsserter.newAssertion()
                                .expectEquals(actual, expected)
                )
                .verifyComplete();
    }
}
