package be.sgerard.i18n.service.repository.github;

import be.sgerard.i18n.AbstractIntegrationTest;
import be.sgerard.i18n.model.repository.dto.GitHubRepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.test.i18n.support.TransactionalReactiveTest;
import be.sgerard.test.i18n.support.WithJaneDoeAdminUser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.Objects;

import static be.sgerard.test.i18n.model.GitRepositoryCreationDtoTestUtils.i18nToolRepositoryCreationDto;

/**
 * @author Sebastien Gerard
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitHubRepositoryHandlerTest extends AbstractIntegrationTest {

    @Autowired
    private GitHubRepositoryHandler handler;

    @Autowired
    private RepositoryManager repositoryManager;

    @BeforeAll
    public void setupRepo() throws Exception {
        gitRepo
                .createMockFor(i18nToolRepositoryCreationDto())
                .allowAnonymousRead()
                .onCurrentGitProject()
                .create();
    }

    @AfterAll
    public void destroy() {
        gitRepo.destroyAll();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void updateAccessKeyNewOne() {
        final String accessKey = "abzec";

        StepVerifier
                .create(
                        repositoryManager
                                .create(i18nToolRepositoryCreationDto())
                                .map(GitHubRepositoryEntity.class::cast)
                                .flatMap(repository ->
                                        handler.updateRepository(
                                                repository,
                                                GitHubRepositoryPatchDto.gitHubBuilder()
                                                        .id(repository.getId())
                                                        .accessKey(accessKey)
                                                        .build()
                                        )
                                )
                )
                .expectNextMatches(entity -> Objects.equals(accessKey, entity.getAccessKey().orElseThrow(null)))
                .expectComplete()
                .verify();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void updateAccessKeyNoChange() {
        final String accessKey = "abzec";

        StepVerifier
                .create(
                        repositoryManager
                                .create(i18nToolRepositoryCreationDto())
                                .map(GitHubRepositoryEntity.class::cast)
                                .flatMap(repository ->
                                        handler.updateRepository(
                                                repository,
                                                GitHubRepositoryPatchDto.gitHubBuilder()
                                                        .id(repository.getId())
                                                        .accessKey(accessKey)
                                                        .build()
                                        )
                                )
                                .flatMap(repository ->
                                        handler.updateRepository(
                                                repository,
                                                GitHubRepositoryPatchDto.gitHubBuilder()
                                                        .id(repository.getId())
                                                        .build()
                                        )
                                )
                )
                .expectNextMatches(entity -> Objects.equals(accessKey, entity.getAccessKey().orElseThrow(null)))
                .expectComplete()
                .verify();
    }

    @Test
    @TransactionalReactiveTest
    @WithJaneDoeAdminUser
    public void updateAccessKeyRemoved() {
        final String accessKey = "abzec";

        StepVerifier
                .create(
                        repositoryManager
                                .create(i18nToolRepositoryCreationDto())
                                .map(GitHubRepositoryEntity.class::cast)
                                .flatMap(repository ->
                                        handler.updateRepository(
                                                repository,
                                                GitHubRepositoryPatchDto.gitHubBuilder()
                                                        .id(repository.getId())
                                                        .accessKey(accessKey)
                                                        .build()
                                        )
                                )
                                .flatMap(repository ->
                                        handler.updateRepository(
                                                repository,
                                                GitHubRepositoryPatchDto.gitHubBuilder()
                                                        .id(repository.getId())
                                                        .accessKey("")
                                                        .build()
                                        )
                                )
                )
                .expectNextMatches(entity -> Objects.equals(null, entity.getAccessKey().orElse(null)))
                .expectComplete()
                .verify();
    }

}
