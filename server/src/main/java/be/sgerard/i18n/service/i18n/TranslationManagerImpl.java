package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.file.BundleWalkingContext;
import be.sgerard.i18n.model.i18n.file.BundleWalkingKeys;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileEntry;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.auth.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.file.BundleHandler;
import be.sgerard.i18n.service.i18n.file.BundleWalker;
import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.i18n.listener.TranslationsListener;
import be.sgerard.i18n.service.locale.TranslationLocaleManager;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.support.ReactiveUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository.*;
import static java.util.Collections.singletonList;
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

    private final BundleKeyEntityRepository translationRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationController authenticationManager;
    private final RepositoryManager repositoryManager;
    private final TranslationsListener listener;
    private final BundleWalker walker;
    private final List<BundleHandler> handlers;
    private final TranslationsSyncStrategy syncStrategy;

    public TranslationManagerImpl(BundleKeyEntityRepository translationRepository,
                                  TranslationLocaleManager localeManager,
                                  AuthenticationController authenticationManager,
                                  RepositoryManager repositoryManager,
                                  TranslationsListener listener,
                                  List<BundleHandler> handlers) {
        this.translationRepository = translationRepository;
        this.localeManager = localeManager;
        this.authenticationManager = authenticationManager;
        this.repositoryManager = repositoryManager;
        this.listener = listener;
        this.walker = new BundleWalker(handlers);
        this.handlers = handlers;
        this.syncStrategy = new DefaultTranslationsSyncStrategy(translationRepository);
    }

    @Override
    public Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return createWalkingContext(workspace, api)
                .flatMapMany(context -> walker.walk((bundleFile, handler) -> onBundleFound(workspace, bundleFile, handler, context), context))
                .collectList()
                .flatMapMany(bundleFiles ->
                        removeOldBundleFiles(bundleFiles, workspace)
                                .thenMany(Flux.fromIterable(bundleFiles))
                );
    }

    @Override
    public Flux<BundleFileEntity> writeTranslations(WorkspaceEntity workspace, TranslationRepositoryWriteApi api) {
        return Flux
                .fromIterable(workspace.getFiles())
                .doOnNext(bundleFile ->
                        logger.info("Updating the bundle file located in [{}] named [{}] with {} file(s) and containing {} translation(s).",
                                bundleFile.getLocation(), bundleFile.getName(), bundleFile.getFiles().size(), bundleFile.getNumberKeys()
                        )
                )
                .flatMap(bundleFile ->
                        localeManager.findAll()
                                .filterWhen(locale -> hasUpdatedTranslations(workspace, bundleFile, locale))
                                .flatMap(locale ->
                                        getHandler(bundleFile.getType())
                                                .updateTranslations(bundleFile.toLocation(), locale, findTranslations(bundleFile, locale), api)
                                )
                                .then(Mono.just(bundleFile))
                );
    }

    @Override
    public Mono<BundleKeyTranslationEntity> updateTranslation(TranslationUpdateDto translationUpdate) throws ResourceNotFoundException {
        return updateTranslations(singletonList(translationUpdate))
                .map(updatedTranslations -> {
                    if (updatedTranslations.size() != 1) {
                        throw new IllegalStateException("One and only one translation is expected. Hint: algorithm issue?");
                    }

                    return updatedTranslations.get(0);
                });
    }

    @Override
    public Mono<List<BundleKeyTranslationEntity>> updateTranslations(List<TranslationUpdateDto> updates) throws ResourceNotFoundException {
        return listener
                .beforeUpdate(updates)
                .doOnNext(ValidationException::throwIfFailed)
                .flatMap(validationResult -> authenticationManager.getCurrentUser().map(AuthenticatedUserDto::getUserId))
                .flatMapMany(currentUser ->
                        Flux
                                .fromIterable(updates)
                                .groupBy(TranslationUpdateDto::getBundleKeyId)
                                .flatMap(updatesByBundleKey ->
                                        updatesByBundleKey
                                                .collectList()
                                                .flatMapMany(updatesForBundleKey ->
                                                        updateBundleKey(currentUser, updatesByBundleKey.key(), updatesForBundleKey)
                                                )
                                )
                )
                .collectList()
                .flatMap(translations -> listener.afterUpdate(translations).thenReturn(translations))
                .map(translations -> translations.stream().map(Pair::getKey).collect(toList()));
    }

    @Override
    public Mono<Void> deleteByWorkspace(WorkspaceEntity workspace) {
        logger.info("Delete all translations of the workspace [{}] alias [{}] that has been deleted.", workspace.getId(), workspace.getBranch());

        return translationRepository.deleteByWorkspace(workspace.getId());
    }

    /**
     * Creates the {@link BundleWalkingContext context} used for walking around repository files.
     */
    private Mono<BundleWalkingContext> createWalkingContext(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return Mono
                .zip(
                        localeManager.findAll().collectList(),
                        createInclusionPredicates(workspace)
                )
                .map(tuple -> new BundleWalkingContext(api, tuple.getT2(), tuple.getT1()));
    }

    /**
     * Creates predicates that will return whether the specified path can be associated to a bundle file.
     */
    private Mono<Map<BundleType, Predicate<Path>>> createInclusionPredicates(WorkspaceEntity workspace) {
        return repositoryManager
                .findByIdOrDie(workspace.getRepository())
                .map(repository ->
                        Stream.of(BundleType.values())
                                .collect(toMap(
                                        type -> type,
                                        type -> (directory) -> repository
                                                .getTranslationsConfiguration()
                                                .getBundle(type)
                                                .map(config -> config.isIncluded(directory))
                                                .orElse(true)
                                ))
                );
    }

    /**
     * Returns the {@link BundleHandler handler} supporting the specified {@link BundleType type}.
     */
    private BundleHandler getHandler(BundleType bundleType) {
        for (BundleHandler handler : handlers) {

            if (handler.support(bundleType)) {
                return handler;
            }
        }

        throw new IllegalStateException("There is no handler supporting [" + bundleType + "].");
    }

    /**
     * Callback that registers all translations found on the specified {@link ScannedBundleFile bundle file}.
     */
    private Mono<BundleFileEntity> onBundleFound(WorkspaceEntity workspace,
                                                 ScannedBundleFile bundleFile,
                                                 BundleHandler handler,
                                                 BundleWalkingContext context) {
        logger.info("A bundle file has been found located in [{}] named [{}] with {} file(s).",
                bundleFile.getLocationDirectory(), bundleFile.getName(), bundleFile.getFiles().size());

        return ReactiveUtils
                .combine(
                        loadLocalTranslations(workspace, bundleFile),
                        loadRemoteTranslations(workspace, bundleFile, handler, context),
                        (fromFile, fromDb) -> fromFile.getKey().compareTo(fromDb.getKey())
                )
                .collectList()
                .flatMap(bundleKeyPairs ->
                        Flux
                                .fromIterable(bundleKeyPairs)
                                .flatMap(syncStrategy::synchronizeLocalAndRemote, 1)
                                .collectList()
                                .map(bundleKeys -> updateBundleFileStats(workspace, bundleFile, bundleKeys))
                );
    }


    /**
     * Finds the {@link BundleKeyEntity bundle key} having the specified id.
     */
    private Mono<BundleKeyEntity> findBundleKeyOrDie(String bundleKeyId) {
        return translationRepository
                .findById(bundleKeyId)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.bundleFileNotFoundException(bundleKeyId)));
    }

    /**
     * Returns all the translations composing the specified {@link BundleFileEntity bundle file}
     * for the specified {@link TranslationLocaleEntity locale}.
     */
    private Flux<Pair<String, String>> findTranslations(BundleFileEntity bundleFile, TranslationLocaleEntity locale) {
        return translationRepository
                .search(
                        new Query()
                                .addCriteria(Criteria.where(FIELD_BUNDLE_FILE).is(bundleFile.getId()))
                                .with(Sort.by(getTranslationField(locale.getId(), TRANSLATION_FIELD_INDEX)))
                )
                .filter(bundleKey -> bundleKey.hasTranslations(locale.getId()))
                .map(bundleKey -> Pair.of(bundleKey.getKey(), bundleKey.getTranslationOrDie(locale.getId()).getValue().orElse(null)));
    }

    /**
     * Updates translations of the specified {@link BundleKeyEntity bundle key}.
     */
    private Flux<Pair<BundleKeyTranslationEntity, BundleKeyEntity>> updateBundleKey(String currentUser, String key, List<TranslationUpdateDto> updates) {
        return findBundleKeyOrDie(key)
                .doOnNext(bundleKey ->
                        updates.forEach(update -> updateTranslation(bundleKey, update, currentUser))
                )
                .flatMap(translationRepository::save)
                .flatMapMany(bundleKey ->
                        Flux
                                .fromIterable(updates)
                                .map(update -> bundleKey.getTranslationOrCreate(update.getLocaleId()))
                                .map(translation -> Pair.of(translation, bundleKey))
                );
    }

    /**
     * Updates the translation of the specified {@link BundleKeyEntity bundle key} using the new updated value.
     */
    private void updateTranslation(BundleKeyEntity bundleKey, TranslationUpdateDto update, String currentUser) {
        final String localeId = update.getLocaleId();
        final String newUpdatedValue = update.getTranslation().map(TranslationFileUtils::mapToNullIfEmpty).orElse(null);
        final BundleKeyTranslationEntity translation = bundleKey.getTranslationOrCreate(localeId);

        translation.setModification(new BundleKeyTranslationModificationEntity(newUpdatedValue, currentUser));
    }

    /**
     * Returns whether the specified bundle for the specified locale is associated to translations that have been updated.
     */
    private Mono<Boolean> hasUpdatedTranslations(WorkspaceEntity workspace, BundleFileEntity bundleFile, TranslationLocaleEntity locale) {
        return translationRepository
                .search(TranslationsSearchRequest.builder()
                        .workspace(workspace.getId())
                        .bundleFile(bundleFile.getId())
                        .locale(locale.getId())
                        .criterion(TranslationSearchCriterion.UPDATED_TRANSLATIONS)
                        .build()
                )
                .hasElements();
    }

    /**
     * Loads all {@link BundleKeyEntity bundle keys} defined in the local database and sorts them by key.
     */
    private Flux<BundleKeyEntity> loadLocalTranslations(WorkspaceEntity workspace,
                                                        ScannedBundleFile bundleFile) {
        final Optional<BundleFileEntity> bundleFileEntity = workspace.getBundleFile(bundleFile);

        if (bundleFileEntity.isEmpty()) {
            return Flux.empty();
        }

        return translationRepository
                .search(
                        TranslationsSearchRequest.builder()
                                .workspace(workspace.getId())
                                .bundleFile(bundleFileEntity.get().getId())
                                .build()
                )
                .sort(Comparator.comparing(BundleKeyEntity::getKey));
    }

    /**
     * Loads all {@link BundleKeyEntity bundle keys} from the specified {@link ScannedBundleFile bundle file} and sorts them by key.
     */
    private Flux<BundleKeyEntity> loadRemoteTranslations(WorkspaceEntity workspace,
                                                         ScannedBundleFile bundleFile,
                                                         BundleHandler handler,
                                                         BundleWalkingContext context) {
        final BundleFileEntity bundleFileEntity = workspace.getOrCreateBundleFile(bundleFile);

        final BundleWalkingKeys bundleKeys = new BundleWalkingKeys();

        return Flux
                .fromIterable(bundleFile.getFiles())
                .map(ScannedBundleFileEntry::getLocale)
                .flatMap(locale ->
                        handler
                                .scanTranslations(bundleFileEntity.toLocation(), locale, context)
                                .index()
                                .map(indexedTranslation ->
                                        bundleKeys
                                                .getOrCreate(workspace, bundleFileEntity, indexedTranslation.getT2().getKey())
                                                .addTranslation(locale.getId(), indexedTranslation.getT1(), indexedTranslation.getT2().getValue())
                                )
                )
                .thenMany(
                        Mono
                                .just(bundleKeys)
                                .flatMapMany(BundleWalkingKeys::stream)
                                .sort(Comparator.comparing(BundleKeyEntity::getKey))
                );
    }

    /**
     * Removes {@link BundleFileEntity bundle files} that are present in the specified {@link WorkspaceEntity workspace},
     * but not in the specified list and returns them.
     * <p>
     * All the translations associated to those files are removed.
     */
    private Flux<BundleFileEntity> removeOldBundleFiles(List<BundleFileEntity> bundleFiles, WorkspaceEntity workspace) {
        final Collection<BundleFileEntity> removedFiles = new ArrayList<>(workspace.getFiles());
        removedFiles.removeAll(bundleFiles);

        workspace.getFiles().retainAll(bundleFiles);

        return Flux
                .fromIterable(removedFiles)
                .doOnNext(removedBundleFile ->
                        logger.info("The bundle file located in [{}] named [{}] with {} file(s) is no more present remotely, remove it.",
                                removedBundleFile.getLocation(), removedBundleFile.getName(), removedBundleFile.getFiles().size())
                )
                .flatMap(removedBundleFile ->
                                translationRepository
                                        .deleteByBundleFile(removedBundleFile.getId())
                                        .thenReturn(removedBundleFile),
                        1
                );
    }

    /**
     * Updates statistics of the specified {@link BundleFileEntity bundle file} based on what has been loaded.
     */
    private BundleFileEntity updateBundleFileStats(WorkspaceEntity workspaceEntity,
                                                   ScannedBundleFile bundleFile,
                                                   List<BundleKeyEntity> bundleKeys) {
        final BundleFileEntity bundleFileEntity = workspaceEntity.getOrCreateBundleFile(bundleFile);

        bundleFileEntity.setNumberKeys(bundleKeys.size());

        logger.info("The bundle file located in [{}] named [{}] with {} file(s) contains {} translation(s).",
                bundleFile.getLocationDirectory(), bundleFile.getName(), bundleFile.getFiles().size(), bundleFileEntity.getNumberKeys());

        return bundleFileEntity;
    }
}
