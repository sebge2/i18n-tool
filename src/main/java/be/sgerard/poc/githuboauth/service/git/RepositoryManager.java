package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.service.LockTimeoutException;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    void initLocalRepository() throws RepositoryException;

    List<String> listBranches() throws RepositoryException, LockTimeoutException;

    <T> T checkoutBranchAndDo(String branchName, RepositoryBrowser<T> repoConsumer) throws RepositoryException, LockTimeoutException;

}
