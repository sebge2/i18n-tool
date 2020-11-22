package be.sgerard.i18n.service.workspace.snapshot;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.TranslationLocaleRepository;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.test.i18n.model.RepositoryEntityTestUtils;
import be.sgerard.test.i18n.model.WorkspaceEntityAsserter;
import be.sgerard.test.i18n.support.CleanupDatabase;
import be.sgerard.test.i18n.support.auth.internal.WithJaneDoeAdminUser;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.io.File;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitHubRepositoryCreationDto;
import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolGitRepositoryCreationDto;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.i18nToolGitHubRepository;
import static be.sgerard.test.i18n.model.RepositoryEntityTestUtils.i18nToolGitRepository;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.enLocale;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.frLocale;
import static be.sgerard.test.i18n.model.WorkspaceEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WorkspaceSnapshotHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private WorkspaceSnapshotHandler handler;

    @Autowired
    private TranslationLocaleRepository localeRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private RepositoryEntityRepository repositoryEntityRepository;

    @TempDir
    public File tempDir;

    @BeforeAll
    public void setupRepo() {
        remoteRepository
                .gitHub()
                .create(i18nToolGitHubRepositoryCreationDto(), "myGitHubRepo")
                .accessToken(RepositoryEntityTestUtils.I18N_TOOL_GITHUB_ACCESS_TOKEN)
                .onCurrentGitProject()
                .start();

        remoteRepository
                .git()
                .create(i18nToolGitRepositoryCreationDto(), "myGitRepo")
                .addUser(RepositoryEntityTestUtils.I18N_TOOL_GIT_REPO_USER, RepositoryEntityTestUtils.I18N_TOOL_GIT_REPO_USER_PASSWORD)
                .onCurrentGitProject()
                .start();

        remoteRepository.gitHub().forHint("myGitHubRepo").manageRemoteBranches().createBranches("develop", "release/2020.08");
        remoteRepository.git().forHint("myGitRepo").manageRemoteBranches().createBranches("develop", "release/2020.08");
    }

    @BeforeEach
    public void setupRepositories() {
        StepVerifier.create(repositoryEntityRepository.save(i18nToolGitHubRepository())).expectNextCount(1).verifyComplete();
        StepVerifier.create(repositoryEntityRepository.save(i18nToolGitRepository())).expectNextCount(1).verifyComplete();

        StepVerifier.create(localeRepository.save(enLocale())).expectNextCount(1).verifyComplete();
        StepVerifier.create(localeRepository.save(frLocale())).expectNextCount(1).verifyComplete();

        StepVerifier.create(workspaceRepository.save(masterI18nToolsGitHub())).expectNextCount(1).verifyComplete();
        StepVerifier.create(workspaceRepository.save(developI18nToolsGitHub())).expectNextCount(1).verifyComplete();
        StepVerifier.create(workspaceRepository.save(release20208I18nToolsGitHub())).expectNextCount(1).verifyComplete();

        StepVerifier.create(workspaceRepository.save(masterI18nToolsGit())).expectNextCount(1).verifyComplete();
        StepVerifier.create(workspaceRepository.save(developI18nToolsGit())).expectNextCount(1).verifyComplete();
        StepVerifier.create(workspaceRepository.save(release20208I18nToolsGit())).expectNextCount(1).verifyComplete();
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

        assertNumberWorkspaces(6);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberWorkspaces(0);

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

        assertNumberWorkspaces(6);

        StepVerifier
                .create(handler.clearAll())
                .verifyComplete();

        assertNumberWorkspaces(0);

        StepVerifier
                .create(handler.restoreAll(tempDir))
                .verifyComplete();

        assertNumberWorkspaces(6);

        assertWorkspace(masterI18nToolsGitHub().getId(), masterI18nToolsGitHub());
        assertWorkspace(developI18nToolsGitHub().getId(), developI18nToolsGitHub());
        assertWorkspace(release20208I18nToolsGitHub().getId(), release20208I18nToolsGitHub());
        assertWorkspace(masterI18nToolsGit().getId(), masterI18nToolsGit());
        assertWorkspace(developI18nToolsGit().getId(), developI18nToolsGit());
        assertWorkspace(release20208I18nToolsGit().getId(), release20208I18nToolsGit());
    }

    private void assertNumberWorkspaces(int expected) {
        StepVerifier
                .create(workspaceRepository.findAll().collectList())
                .expectNextMatches(actual -> actual.size() == expected)
                .verifyComplete();
    }

    private void assertWorkspace(String workspaceId, WorkspaceEntity expected) {
        StepVerifier.create(workspaceRepository.findById(workspaceId))
                .assertNext(actual ->
                        WorkspaceEntityAsserter.newAssertion()
                                .expectEquals(actual, expected)
                )
                .verifyComplete();
    }
}
