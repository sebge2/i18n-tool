package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.model.git.PullRequestStatus;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
public interface PullRequestManager {

    int createRequest(String message, String currentBranch, String targetBranch) throws Exception;

    List<Integer> listRequests() throws Exception;

    PullRequestStatus getStatus(int requestNumber) throws Exception;

}
