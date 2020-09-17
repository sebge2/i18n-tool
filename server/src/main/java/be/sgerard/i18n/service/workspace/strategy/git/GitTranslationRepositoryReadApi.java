package be.sgerard.i18n.service.workspace.strategy.git;

import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import be.sgerard.i18n.service.repository.RepositoryException;
import be.sgerard.i18n.service.repository.git.GitRepositoryApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.InputStream;

/**
 * {@link TranslationRepositoryReadApi API} for Git repository.
 *
 * @author Sebastien Gerard
 */
public class GitTranslationRepositoryReadApi implements TranslationRepositoryReadApi {

    private final GitRepositoryApi api;

    public GitTranslationRepositoryReadApi(GitRepositoryApi api, String branch) {
        this.api = api
                .resetHardHead()
                .checkoutDefaultBranch()
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

    @Override
    public Mono<InputStream> openInputStream(File file) throws RepositoryException {
        return Mono.just(api.openInputStream(file));
    }

    @Override
    public Mono<File> openAsTemp(File file) throws RepositoryException {
        return Mono.just(api.openAsTemp(file, true));
    }
}
