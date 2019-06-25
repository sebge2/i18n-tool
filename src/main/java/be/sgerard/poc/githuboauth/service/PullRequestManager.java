package be.sgerard.poc.githuboauth.service;

import java.util.List;

/**
 * @author Sebastien Gerard
 */
public interface PullRequestManager {

    int createRequest(String message, String currentBranch, String targetBranch) throws Exception;

    List<Integer> listRequests() throws Exception;

    String getStatus(int requestNumber) throws Exception;

}
