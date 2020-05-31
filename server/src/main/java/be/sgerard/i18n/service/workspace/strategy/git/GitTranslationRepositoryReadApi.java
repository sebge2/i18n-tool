package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * {@link TranslationRepositoryReadApi API} for Git repository.
 *
 * @author Sebastien Gerard
 */
public class GitTranslationRepositoryReadApi implements TranslationRepositoryReadApi {

    private final GitRepositoryApi api;

    public GitTranslationRepositoryReadApi(GitRepositoryApi api, String branch) {
        this.api = api
                .pull()
                .checkout(branch)
                .pull();
    }

    @Override
    public Flux<File> listAllFiles(File file) {
        return Flux.fromStream(api.listAllFiles(file));
    }

    @Override
    public Flux<File> listNormalFiles(File file) {
        return Flux.fromStream(api.listNormalFiles(file));
    }

    @Override
    public Flux<File> listDirectories(File file) {
        return Flux.fromStream(api.listDirectories(file));
    }
}
