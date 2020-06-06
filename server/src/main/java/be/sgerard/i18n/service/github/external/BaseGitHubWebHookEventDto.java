package be.sgerard.i18n.service.github.external;

/**
 * Base DTO for all supported GitHub Web-hook events.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitHubWebHookEventDto {

    private final GitHubRepositoryWebHookDto repository;

    protected BaseGitHubWebHookEventDto(GitHubRepositoryWebHookDto repository) {
        this.repository = repository;
    }

    /**
     * Returns the {@link GitHubRepositoryWebHookDto repository} involved in this event.
     */
    public GitHubRepositoryWebHookDto getRepository() {
        return repository;
    }
}
