package be.sgerard.poc.githuboauth.service;

import be.sgerard.poc.githuboauth.service.auth.AuthenticationManager;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class RepositoryManagerImpl implements RepositoryManager {

    public static final String DEFAULT_BRANCH = "master";
    public static final String REFS_ORIGIN_PREFIX = "refs/remotes/origin/";

    private final String repoUri;
    private final AuthenticationManager authenticationManager;

    private Git localRepository;

    public RepositoryManagerImpl(@Value("${poc.repo-uri}") String repoUri,
                                 AuthenticationManager authenticationManager) {
        this.repoUri = repoUri;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public void initializeLocalRepo(File localRepository) throws Exception {
        if (localRepository.exists() && FileUtils.sizeOfDirectory(localRepository) > 0) {
            throw new IllegalArgumentException("The repository [" + localRepository + "] is already initialized.");
        }

        this.localRepository = Git.cloneRepository()
                .setCredentialsProvider(createProvider())
                .setURI(repoUri)
                .setDirectory(localRepository)
                .setBranchesToClone(singletonList(DEFAULT_BRANCH))
                .setBranch(DEFAULT_BRANCH)
                .call();
    }

    @Override
    public List<String> listBranches() throws Exception {
        return getLocalRepository().branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().stream()
                .map(Ref::getName)
                .map(name -> name.startsWith(REFS_ORIGIN_PREFIX) ? name.substring(REFS_ORIGIN_PREFIX.length()) : name)
                .sorted((first, second) -> {
                    if (DEFAULT_BRANCH.equals(first)) {
                        return -1;
                    } else if (DEFAULT_BRANCH.equals(second)) {
                        return 1;
                    } else {
                        return first.compareTo(second);
                    }
                })
                .distinct()
                .collect(toList());
    }

    @Override
    public void pull() throws Exception {
//        git.pull().setCredentialsProvider(createProvider()).call();
    }

    @Override
    public void createBranch(String branchName) throws Exception {
//        git.checkout()
//                .setCreateBranch(true)
//                .setName(branchName)
//                .call();
    }

    @Override
    public void checkoutBranch(String branchName) throws Exception {
//        git.checkout().setName(branchName).call();
    }

    @Override
    public void commitAll(String message) throws Exception {
//        git.add().addFilepattern("*").call();
//
//        git.commit()
//                .setAuthor("toto", "toto@emasphere.com")
//                .setMessage(message).call();
    }

    @Override
    public void push() throws Exception {
//        git.push().call();
    }

    private UsernamePasswordCredentialsProvider createProvider() {
        return new UsernamePasswordCredentialsProvider(authenticationManager.getCurrentAuth().getToken(), "");
    }

    private Git getLocalRepository() {
        if (localRepository == null) {
            throw new IllegalStateException("The local repository has not been initialized. Hint: call initialize.");
        }

        return localRepository;
    }
}
