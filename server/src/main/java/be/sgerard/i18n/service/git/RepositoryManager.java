package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.repository.RepositoryDescriptionDto;
import be.sgerard.i18n.service.LockTimeoutException;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    RepositoryDescriptionDto getDescription() throws RepositoryException;

    boolean initLocalRepository() throws LockTimeoutException, RepositoryException;

    void open(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException;

    void openInNewTx(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException;

    <T> T open(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException;

    <T> T openInNewTx(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException;

    @FunctionalInterface
    interface ApiConsumer {

        void consume(RepositoryAPI api) throws RepositoryException, LockTimeoutException;

    }

    @FunctionalInterface
    interface ApiTransformer<T> {

        T transform(RepositoryAPI api) throws RepositoryException, LockTimeoutException;

    }


}
