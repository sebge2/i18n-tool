package be.sgerard.i18n.service.git;

import be.sgerard.i18n.service.LockTimeoutException;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.GitAPI;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    void open(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException;

    void openInNewTx(ApiConsumer apiConsumer) throws RepositoryException, LockTimeoutException;

    <T> T open(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException;

    <T> T openInNewTx(ApiTransformer<T> apiConsumer) throws RepositoryException, LockTimeoutException;

    @FunctionalInterface
    interface ApiConsumer {

        void consume(GitAPI api) throws RepositoryException, LockTimeoutException;

    }

    @FunctionalInterface
    interface ApiTransformer<T> {

        T transform(GitAPI api) throws RepositoryException, LockTimeoutException;

    }


}
