package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.CommitRequest;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Sebastien Gerard
 */
public interface RepositoryManager {

    void initLocalRepository() throws RepositoryException;

    List<String> listBranches() throws RepositoryException, LockTimeoutException;

    void browseBranch(String branchName, Consumer<BranchBrowsingAPI> apiConsumer) throws RepositoryException, LockTimeoutException;

    String modifyBranchAndPush(String branchName,
                               CommitRequest commitRequest,
                               Consumer<BranchModificationAPI> apiConsumer) throws RepositoryException, LockTimeoutException;

}
