package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.service.i18n.TranslationRepositoryWriteApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;

import java.util.Objects;

/**
 * {@link TranslationRepositoryWriteApi API} for Git repository. This implementation supports the
 * feature where modifications will be written on a target branch based on an original branch.
 *
 * @author Sebastien Gerard
 */
public class GitTranslationRepositoryWriteApi implements TranslationRepositoryWriteApi {

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
}
