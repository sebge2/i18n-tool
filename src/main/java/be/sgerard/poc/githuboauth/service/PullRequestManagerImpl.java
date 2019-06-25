package be.sgerard.poc.githuboauth.service;

import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import com.jcabi.github.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class PullRequestManagerImpl implements PullRequestManager {

    private static final Pattern REPOSITORY_REGEX = Pattern.compile("^https://github.com/(.+)/(.+).git$");

    private final String repositoryName;
    private final AuthenticationManager authenticationManager;

    public PullRequestManagerImpl(@Value("${poc.repo-uri}") String repoUri,
                                  AuthenticationManager authenticationManager) {
        this.repositoryName = getRepositoryName(repoUri);
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
    public String getStatus(int requestNumber) throws Exception {
        return openRepo().pulls().get(requestNumber).json().getString("state");
    }

    private Repo openRepo() {
        final Github github = openGitHub();

        return github.repos().get(new Coordinates.Simple(repositoryName));
    }

    private RtGithub openGitHub() {
        return new RtGithub(authenticationManager.getCurrentAuth().getToken());
    }

    private String getRepositoryName(String repoUri) {
        final Matcher matcher = REPOSITORY_REGEX.matcher(repoUri);
        if (!matcher.matches()) {

        }

        return matcher.group(1) + "/" + matcher.group(2);
    }
}
