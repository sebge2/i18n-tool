package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.OutputStream;
import java.util.Objects;

/**
 * {@link TranslationRepositoryWriteApi API} for Git repository. This implementation supports the
 * feature where modifications will be written on a target branch based on an original branch.
 *
 * @author Sebastien Gerard
 */
public class GitTranslationRepositoryWriteApi implements TranslationRepositoryWriteApi {

    /**
     * Generates a new unique branch name by adding indexed suffix to make it unique if needed.
     */
    public static String generateUniqueBranch(String name, GitRepositoryApi api) {
        if (!api.listRemoteBranches().contains(name) && !api.listLocalBranches().contains(name)) {
            return name;
        }

        String generatedName = name;
        int index = 0;
        while (api.listRemoteBranches().contains(generatedName) || api.listLocalBranches().contains(generatedName)) {
            generatedName = name + "_" + (++index);
        }

        return generatedName;
    }

    private final GitRepositoryApi api;

    public GitTranslationRepositoryWriteApi(GitRepositoryApi api, String original, String target) {
        this.api = api
                .pull()
                .checkout(original)
                .pull();

        if (!Objects.equals(original, target)) {
            this.api
                    .createBranch(target);
        }
    }

    @Override
    public Mono<File> openAsTemp(File file) throws RepositoryException {
        return Mono.just(api.openAsTemp(file));
    }

    @Override
    public Mono<OutputStream> openOutputStream(File file) throws RepositoryException {
        return Mono.just(api.openOutputStream(file));
    }
}
