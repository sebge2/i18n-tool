package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.service.i18n.TranslationRepositoryReadApi;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Walker of translation bundle files.
 *
 * @author Sebastien Gerard
 */
public class TranslationBundleWalker {

    private final List<TranslationBundleHandler> handlers;

    public TranslationBundleWalker(List<TranslationBundleHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Browses the repository using the specified {@link TranslationRepositoryReadApi API} using the specified
     * {@link TranslationBundleHandler handler} that indicates where are those translations.
     */
    public Flux<BundleFileEntity> walk(WorkspaceEntity workspace, TranslationRepositoryReadApi api, TranslationBundleConsumer consumer) {
        return walk(new File("/"), workspace, api, consumer, handlers);
    }

    /**
     * Browses the specified directory in the repository using the remaining {@link TranslationBundleHandler bundle handlers}
     * that may still find some translations.
     */
    private Flux<BundleFileEntity> walk(File directory,
                                        WorkspaceEntity workspace,
                                        TranslationRepositoryReadApi api,
                                        TranslationBundleConsumer consumer,
                                        List<TranslationBundleHandler> handlers) {
        final List<TranslationBundleHandler> updatedHandlers = handlers.stream()
                .filter(handler -> handler.continueScanning(workspace, directory))
                .collect(toList());

        if (updatedHandlers.isEmpty()) {
            return Flux.empty();
        }

        return Flux.concat(
                Flux
                        .fromIterable(updatedHandlers)
                        .flatMap(handler ->
                                handler
                                        .scanBundles(directory, api)
                                        .flatMap(bundle -> consumer.onBundleFound(bundle, handler.scanKeys(bundle, api)))
                        ),
                api
                        .listDirectories(directory)
                        .flatMap(subDir -> walk(subDir, workspace, api, consumer, updatedHandlers))
        );
    }


    /**
     * Callback consuming translation bundle files that have been found.
     */
    public interface TranslationBundleConsumer {

        /**
         * Callbacks when the specified {@link ScannedBundleFileDto bundle file} has been found containing
         * the specified {@link ScannedBundleFileKeyDto keys}.
         */
        Mono<BundleFileEntity> onBundleFound(ScannedBundleFileDto bundleFile, List<ScannedBundleFileKeyDto> keys);

    }

}
