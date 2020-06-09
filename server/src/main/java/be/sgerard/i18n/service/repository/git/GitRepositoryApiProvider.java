package be.sgerard.i18n.service.repository.git;

/**
 * Provider of {@link GitRepositoryApi Git API}.
 *
 * @author Sebastien Gerard
 */
public interface GitRepositoryApiProvider {

    /**
     * Returns the API for the specified configuration.
     */
    GitRepositoryApi createApi(GitRepositoryApi.Configuration configuration);

}
