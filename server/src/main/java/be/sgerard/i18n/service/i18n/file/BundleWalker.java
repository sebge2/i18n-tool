package be.sgerard.i18n.service.i18n.file;

import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
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
public class BundleWalker {

    private final List<BundleHandler> handlers;

    public BundleWalker(List<BundleHandler> handlers) {
        this.handlers = handlers;
    }

    /**
     * Browses the repository using the specified {@link TranslationRepositoryReadApi API} using the specified
     * {@link BundleHandler handler} that indicates where are those translations.
     */
    public Flux<BundleFileEntity> walk(TranslationBundleConsumer consumer, BundleWalkContext context) {
        return walk(new File("/"), consumer, context, handlers);
    }

    /**
     * Browses the specified directory in the repository using the remaining {@link BundleHandler bundle handlers}
     * that may still find some translations.
     */
    private Flux<BundleFileEntity> walk(File directory,
                                        TranslationBundleConsumer consumer,
                                        BundleWalkContext context,
                                        List<BundleHandler> handlers) {
        final List<BundleHandler> updatedHandlers = handlers.stream()
                .filter(handler -> handler.continueScanning(directory, context))
                .collect(toList());

        if (updatedHandlers.isEmpty()) {
            return Flux.empty();
        }

        return Flux.concat(
                Flux
                        .fromIterable(updatedHandlers)
                        .flatMap(handler ->
                                handler
                                        .scanBundles(directory, context)
                                        .flatMap(bundle -> consumer.onBundleFound(bundle, handler.scanKeys(bundle, context.getApi())))
                        ),
                context.getApi()
                        .listDirectories(directory)
                        .flatMap(subDir -> walk(subDir, consumer, context, updatedHandlers))
        );
    }


    /**
     * Callback consuming translation bundle files that have been found.
     */
    public interface TranslationBundleConsumer {

        /**
         * Callbacks when the specified {@link ScannedBundleFile bundle file} has been found containing
         * the specified {@link ScannedBundleFileKey keys}.
         */
        Mono<BundleFileEntity> onBundleFound(ScannedBundleFile bundleFile, List<ScannedBundleFileKey> keys);

    }

}
