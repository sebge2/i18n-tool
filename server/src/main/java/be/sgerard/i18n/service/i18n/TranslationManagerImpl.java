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
import be.sgerard.i18n.model.i18n.persistence.*;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.file.BundleHandler;
import be.sgerard.i18n.service.i18n.file.BundleWalker;
import be.sgerard.i18n.service.i18n.file.TranslationFileUtils;
import be.sgerard.i18n.service.i18n.listener.TranslationsListener;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.IntStream;
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
    }

    @Override
    public Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        // TODO what to do with bundle files that do not exist any more
        return createWalkingContext(workspace, api)
                .flatMapMany(context -> walker.walk((bundleFile, handler) -> onBundleFound(workspace, bundleFile, handler, context), context));
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
    public Mono<List<BundleKeyTranslationEntity>> updateTranslations(List<TranslationUpdateDto> translations) throws ResourceNotFoundException {
        return listener
                .beforeUpdate(translations)
                .doOnNext(ValidationException::throwIfFailed)
                .flatMap(validationResult -> authenticationManager.getCurrentUser().map(AuthenticatedUserDto::getUserId))
                .flatMapMany(currentUser ->
                        Flux
                                .fromIterable(translations)
                                .flatMap(update ->
                                        findBundleKeyOrDie(update.getBundleKeyId())
                                                .map(bundleKey -> updateTranslation(bundleKey, update, currentUser)) // TODO do a better association between 2 lists
                                )
                                .flatMap(translationRepository::save)
                )
                .collectList()
                .flatMap(bundleKeys ->
                        listener
                                .afterUpdate(bundleKeys, translations)
                                .thenReturn(extractTranslationEntities(bundleKeys, translations))
                );
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
    private Mono<BundleFileEntity> onBundleFound(WorkspaceEntity workspaceEntity,
                                                 ScannedBundleFile bundleFile,
                                                 BundleHandler handler,
                                                 BundleWalkingContext context) {
        logger.info("A bundle file has been found located in [{}] named [{}] with {} file(s).",
                bundleFile.getLocationDirectory(), bundleFile.getName(), bundleFile.getFiles().size());

        return ReactiveUtils
                .combine(
                        loadBundleFileKeys(workspaceEntity, bundleFile, handler, context),
                        Flux.<BundleKeyEntity>empty(),
                        (fromFile, fromDb) -> fromFile.getKey().compareTo(fromDb.getKey())
                )
                .map(matching -> matching.getLeft()) // TODO
                .flatMap(translationRepository::save)
                .collectList()
                .map(bundleKeys -> updateBundleFileStats(workspaceEntity, bundleFile, bundleKeys));
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
     * Updates the translation of the specified {@link BundleKeyEntity bundle key} using the new updated value.
     */
    private BundleKeyEntity updateTranslation(BundleKeyEntity bundleKey, TranslationUpdateDto update, String currentUser) {
        final String localeId = update.getLocaleId();
        final String newUpdatedValue = update.getTranslation().map(TranslationFileUtils::mapToNullIfEmpty).orElse(null);
        final BundleKeyTranslationEntity translation = bundleKey.getTranslationOrCreate(localeId);

        final String currentUpdatedValue =
                translation.getModification().flatMap(BundleKeyTranslationModificationEntity::getUpdatedValue).orElse(null);

        final String originalValue = translation.getOriginalValue().orElse(null);

        if (newUpdatedValue != null) {
            if (Objects.equals(newUpdatedValue, originalValue)) {
                translation.setModification(null);
            } else if (!Objects.equals(currentUpdatedValue, newUpdatedValue)) {
                translation.setModification(new BundleKeyTranslationModificationEntity(newUpdatedValue, currentUser));
            } else {
                // nothing to do, the update match the previous update
            }
        } else {
            translation.setModification(null);
        }

        return bundleKey;
    }

    /**
     * Extracts {@link BundleKeyTranslationEntity translations} that have been updated from the {@link BundleKeyEntity bundle keys}.
     */
    private List<BundleKeyTranslationEntity> extractTranslationEntities(List<BundleKeyEntity> bundleKeys, List<TranslationUpdateDto> translations) {
        return IntStream.range(0, translations.size())
                .mapToObj(i -> bundleKeys.get(i).getTranslationOrCreate(translations.get(i).getLocaleId()))
                .collect(toList());
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
     * Loads all {@link BundleKeyEntity bundle keys} from the specified {@link ScannedBundleFile bundle file} and sorts
     * them by key.
     */
    private Flux<BundleKeyEntity> loadBundleFileKeys(WorkspaceEntity workspaceEntity,
                                                     ScannedBundleFile bundleFile,
                                                     BundleHandler handler,
                                                     BundleWalkingContext context) {
        final BundleFileEntity bundleFileEntity = workspaceEntity.getOrCreateBundleFile(bundleFile);

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
                                                .getOrCreate(workspaceEntity, bundleFileEntity, indexedTranslation.getT2().getKey())
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
