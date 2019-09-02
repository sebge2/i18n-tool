package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.repository.RepositoryDescriptionDto;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    RepositoryDescriptionDto getDescription();

    void initLocalRepository() throws RepositoryException;

    void open(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException;

    <T> T open(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException;

    @FunctionalInterface
    interface ApiConsumer {

        void consume(RepositoryAPI api) throws RepositoryException, LockTimeoutException;

    }

    @FunctionalInterface
    interface ApiTransformer<T> {

        T transform(RepositoryAPI api) throws RepositoryException, LockTimeoutException;

    }


}
