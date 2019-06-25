package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import be.sgerard.poc.githuboauth.model.git.PullRequestStatus;
import com.jcabi.github.*;
import org.springframework.stereotype.Service;

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
    public int createRequest(String message, String currentBranch, String targetBranch) throws Exception {
        return openRepo().pulls().create(message, currentBranch, targetBranch).number();
    }

    @Override
    public List<Integer> listRequests() {
        return StreamSupport.stream(openRepo().pulls().iterate(emptyMap()).spliterator(), false)
            .map(Pull::number)
            .collect(toList());
    }

    @Override
    public PullRequestStatus getStatus(int requestNumber) throws Exception {
        return PullRequestStatus.valueOf(openRepo().pulls().get(requestNumber).json().getString("state"));
    }

    private Repo openRepo() {
        final Github github = openGitHub();

        return github.repos().get(new Coordinates.Simple(repositoryName));
    }

    private Github openGitHub() {
        return new RtGithub(authenticationManager.getCurrentAuth().getToken());
    }

}
