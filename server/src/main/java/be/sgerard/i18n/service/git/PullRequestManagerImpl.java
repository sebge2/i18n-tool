package be.sgerard.i18n.service.git;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.git.PullRequestStatus;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import com.jcabi.github.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class PullRequestManagerImpl implements PullRequestManager {

    private final String repositoryName;
    private final AuthenticationManager authenticationManager;

    public PullRequestManagerImpl(AppProperties appProperties,
                                  AuthenticationManager authenticationManager) {
        this.repositoryName = appProperties.getRepoFqnName();
        this.authenticationManager = authenticationManager;
    }

    @Override
    public int createRequest(String message, String currentBranch, String targetBranch) throws RepositoryException {
        try {
            return openRepo().pulls().create(message, currentBranch, targetBranch).number();
        } catch (IOException e) {
            throw new RepositoryException("Error while creating pull request from branch [" + currentBranch + "].", e);
        }
    }

    @Override
    public List<Integer> listRequests() {
        return StreamSupport.stream(openRepo().pulls().iterate(emptyMap()).spliterator(), false)
                .map(Pull::number)
                .collect(toList());
    }

    @Override
    public PullRequestStatus getStatus(int requestNumber) throws RepositoryException {
        try {
            return PullRequestStatus.fromString(openRepo().pulls().get(requestNumber).json().getString("state"));
        } catch (IOException e) {
            throw new RepositoryException("Error while retrieving the status of the pull request " + requestNumber + ".", e);
        }
    }

    private Repo openRepo() {
        final Github github = openGitHub();

        return github.repos().get(new Coordinates.Simple(repositoryName));
    }

    private Github openGitHub() {
        return new RtGithub(authenticationManager.getGitHubToken());
    }

}
