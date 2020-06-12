package be.sgerard.test.i18n.helper;

import be.sgerard.i18n.model.repository.dto.GitHubRepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.GitRepositoryCreationDto;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApiProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static be.sgerard.i18n.support.GitHubUtils.createGitHubUrl;
import static be.sgerard.test.i18n.support.TestUtils.currentProjectLocation;

/**
 * @author Sebastien Gerard
 */
@Component
@Primary
public class GitRepositoryMockTestHelper implements GitRepositoryApiProvider {

    private final Set<GitRepositoryMock> repositories = new HashSet<>();

    public GitRepositoryMockTestHelper() {
    }

    @Override
    public GitRepositoryApi createApi(GitRepositoryApi.Configuration configuration) {
        return new GitRepositoryApiMock(
                findRepoByUri(configuration.getRemoteUri().orElse(null)).orElse(null),
                configuration
        );
    }

    public StepSetup createMockFor(URI remoteUri) {
        return new StepSetup(remoteUri);
    }

    public StepSetup createMockFor(GitHubRepositoryCreationDto creationDto) {
        return createMockFor(createGitHubUrl(creationDto.getUsername(), creationDto.getRepository()));
    }

    public StepSetup createMockFor(GitRepositoryCreationDto creationDto) {
        return createMockFor(creationDto.getLocationAsURI());
    }

    public void destroyAll() {
        this.repositories.forEach(GitRepositoryMock::destroy);
        this.repositories.clear();
    }

    public GitRepositoryMock getRepo(GitHubRepositoryCreationDto creationDto) {
        return findRepoByUriOrDie(createGitHubUrl(creationDto.getUsername(), creationDto.getRepository()));
    }

    public GitRepositoryMock getRepo(GitRepositoryCreationDto creationDto) {
        return findRepoByUriOrDie(URI.create(creationDto.getLocation()));
    }

    private Optional<GitRepositoryMock> findRepoByUri(URI remoteUri) {
        return repositories.stream()
                .filter(repository -> Objects.equals(repository.getMockedRemoteUri(), remoteUri))
                .findFirst();
    }

    private GitRepositoryMock findRepoByUriOrDie(URI remoteUri) {
        return findRepoByUri(remoteUri)
                .orElseThrow(() -> new IllegalArgumentException("There is no mock repository for [" + remoteUri + "]"));
    }

    public class StepSetup {

        private final GitRepositoryMock.Builder builder;

        public StepSetup(URI remoteUri) {
            this.builder = GitRepositoryMock.builder()
                    .remoteUri(remoteUri);
        }

        public StepSetup allowAnonymousRead() {
            this.builder.allowAnonymousRead(true);
            return this;
        }

        public StepSetup user(String user, String password) {
            builder.users(new GitRepositoryMock.User(user, password));
            return this;
        }

        public StepSetup userKey(String userKey) {
            builder.userKeys(new GitRepositoryMock.UserKey(userKey));
            return this;
        }

        public StepFinalize baseOnCurrentGitProject() {
            return basedOnGitDirectory(currentProjectLocation());
        }

        public StepFinalize basedOnGitDirectory(File originalDirectory) {
            return new StepFinalize(builder.originalDirectory(originalDirectory));
        }
    }

    public class StepFinalize {

        private final GitRepositoryMock.Builder builder;

        public StepFinalize(GitRepositoryMock.Builder builder) {
            this.builder = builder;
        }

        public GitRepositoryMock create() throws Exception {
            final GitRepositoryMock repository = builder.build();

            repositories.stream()
                    .filter(existing -> Objects.equals(existing.getMockedRemoteUri(), repository.getMockedRemoteUri()))
                    .findFirst()
                    .ifPresent(GitRepositoryMock::destroy);

            repository.init();

            repositories.add(repository);

            return repository;
        }

    }
}
