package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.file.BundleHandler;
import be.sgerard.i18n.service.i18n.file.BundleWalker;
import be.sgerard.i18n.service.i18n.listener.TranslationsListener;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Implementation of the {@link TranslationManager translation manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationManagerImpl implements TranslationManager {

    private static final Logger logger = LoggerFactory.getLogger(TranslationManagerImpl.class);

    private final BundleKeyTranslationRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationController authenticationManager;
    private final TranslationsListener listener;
    private final BundleWalker walker;
    private final List<BundleHandler> handlers;

    public TranslationManagerImpl(BundleKeyTranslationRepository keyEntryRepository,
                                  TranslationLocaleManager localeManager,
                                  AuthenticationController authenticationManager,
                                  TranslationsListener listener,
                                  List<BundleHandler> handlers) {
        this.keyEntryRepository = keyEntryRepository;
        this.localeManager = localeManager;
        this.authenticationManager = authenticationManager;
        this.listener = listener;
        this.walker = new BundleWalker(handlers);
        this.handlers = handlers;
    }

    @Override
    public Mono<BundleKeyTranslationEntity> findTranslation(String id) {
        return Mono.justOrEmpty(keyEntryRepository.findById(id));
    }

    @Override
    @Transactional
    public Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return createContext(workspace, api)
                .flatMapMany(context -> walker.walk((bundleFile, entries) -> onBundleFound(workspace, bundleFile, entries), context));
    }

    @Override
    @Transactional
    public Mono<Void> writeTranslations(WorkspaceEntity workspace, TranslationRepositoryWriteApi api) {
        return Flux
                .fromIterable(workspace.getFiles())
                .flatMap(file -> {
                    final ScannedBundleFile bundleFile = new ScannedBundleFile(file);

                    // TODO stream per language
                    return getHandler(bundleFile).updateBundle(bundleFile, getTranslations(file), api);
                })
                .then();
    }

    @Override
    @Transactional
    public Flux<BundleKeyTranslationEntity> updateTranslations(Map<String, String> translations) throws ResourceNotFoundException {
        final UserDto currentUser = authenticationManager.getCurrentUser().getUser();

        return Flux
                .fromIterable(translations.entrySet())
                .flatMap(entry ->
                        findTranslationOrDie(entry.getKey())
                                .map(entity -> Pair.of(entity, entry.getValue()))
                )
                .flatMap(entry -> listener
                        .beforeUpdate(entry.getKey(), entry.getValue())
                        .map(validationResult -> {
                            ValidationException.throwIfFailed(validationResult);

                            return entry;
                        })
                )
                .doOnNext(entry -> {
                    final BundleKeyTranslationEntity translation = entry.getLeft();

                    translation.setUpdatedValue(mapToNullIfEmpty(entry.getValue()));

                    if (Objects.equals(translation.getOriginalValue(), translation.getUpdatedValue())) {
                        translation.setUpdatedValue(null);
                    }

                    translation.setLastEditor(translation.getUpdatedValue().isEmpty() ? null : currentUser.getId());
                })
                .map(Pair::getKey)
                .flatMap(updates ->
                        listener.afterUpdate(updates)
                                .thenReturn(updates)
                );
    }

    /**
     * Creates the {@link BundleWalkContext context} used for walking around repository files.
     */
    private Mono<BundleWalkContext> createContext(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return localeManager
                .findAll()
                .collectList()
                .map(locales -> new BundleWalkContext(api, createInclusionPredicates(workspace), locales));
    }

    /**
     * Creates predicates that will return whether the specified path can be associated to bundle file having the specified type.
     */
    private Map<BundleType, Predicate<Path>> createInclusionPredicates(WorkspaceEntity workspace) {
        return Stream.of(BundleType.values())
                .collect(toMap(
                        type -> type,
                        type -> (directory) -> workspace
                                .getRepository()
                                .getTranslationsConfiguration()
                                .getBundle(type)
                                .map(config -> config.isIncluded(directory))
                                .orElse(true)
                ));
    }

    /**
     * Returns the {@link BundleHandler handler} supporting the specified {@link ScannedBundleFile file}.
     */
    private BundleHandler getHandler(ScannedBundleFile bundleFile) {
        for (BundleHandler handler : handlers) {

            if (handler.support(bundleFile.getType())) {
                return handler;
            }
        }

        throw new IllegalStateException("There is no handler supporting [" + bundleFile + "].");
    }

    /**
     * Callback that registers all translations found on the specified {@link ScannedBundleFile bundle file}.
     */
    private Mono<BundleFileEntity> onBundleFound(WorkspaceEntity workspaceEntity,
                                                 ScannedBundleFile bundleFile,
                                                 Flux<ScannedBundleFileKey> keys) {
        logger.info("A bundle file has been found located in [{}] named [{}] with {} file(s).", bundleFile.getLocationDirectory(), bundleFile.getName(), bundleFile.getFiles().size());

        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(
                        workspaceEntity,
                        bundleFile.getName(),
                        bundleFile.getLocationDirectory().toString(),
                        bundleFile.getType(),
                        bundleFile.getFiles().stream().map(BundleFileEntryEntity::new).collect(toList())
                );

        return keys
                .doOnNext(entry -> {
                    final BundleKeyEntity keyEntity = new BundleKeyEntity(bundleFileEntity, entry.getKey());

                    for (ScannedBundleFileEntry file : bundleFile.getFiles()) {
                        new BundleKeyTranslationEntity(keyEntity, file.getLocale(), mapToNullIfEmpty(entry.getTranslations().get(file.getLocale())));
                    }
                })
                .then(Mono.just(bundleFileEntity));
    }

    /**
     * Returns all the {@link ScannedBundleFileKey keys} composing the specified {@link BundleFileEntity bundle file}.
     */
    private List<ScannedBundleFileKey> getTranslations(BundleFileEntity file) {
        return file.getKeys().stream()
                .map(
                        keyEntity ->
                                new ScannedBundleFileKey(
                                        keyEntity.getKey(),
                                        keyEntity.getTranslations().stream()
                                                .map(keyEntryEntity -> Pair.of(keyEntryEntity.getLocale(), keyEntryEntity.getValue().orElse(null)))
                                                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll)
                                )
                )
                .collect(toList());
    }
}
