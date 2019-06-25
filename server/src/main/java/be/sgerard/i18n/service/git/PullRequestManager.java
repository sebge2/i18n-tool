package be.sgerard.i18n.service.git;

import be.sgerard.i18n.model.git.PullRequestStatus;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
public interface PullRequestManager {

    int createRequest(String message, String currentBranch, String targetBranch) throws RepositoryException;

    List<Integer> listRequests() throws RepositoryException;

    PullRequestStatus getStatus(int requestNumber) throws RepositoryException;

}
