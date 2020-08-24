package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkingContext;
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
import be.sgerard.i18n.service.i18n.listener.TranslationsListener;
import be.sgerard.i18n.service.repository.RepositoryManager;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository.*;
import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
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
    @Transactional
    public Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return createWalkingContext(workspace, api)
                .flatMapMany(context -> walker.walk((bundleFile, handler) -> onBundleFound(workspace, bundleFile, handler, context), context));
    }

    @Override
    @Transactional
    public Mono<Void> writeTranslations(WorkspaceEntity workspace, TranslationRepositoryWriteApi api) {
        return Flux
                .fromIterable(workspace.getFiles())
                .flatMap(bundleFile ->
                        localeManager.findAll()
                                .map(locale ->
                                        getHandler(bundleFile.getType())
                                                .updateTranslations(
                                                        bundleFile.toLocation(),
                                                        locale,
                                                        findTranslations(bundleFile, locale),
                                                        api
                                                )
                                )
                )
                .then();
    }

    @Override
    @Transactional
    public Mono<BundleKeyEntity> updateTranslation(String bundleKeyId, String localeId, String value) throws ResourceNotFoundException {
        return Mono
                .zip(
                        authenticationManager.getCurrentUser().map(AuthenticatedUserDto::getUserId),
                        findBundleKeyOrDie(bundleKeyId)
                )
                .map(pair -> Pair.of((String) pair.get(0), (BundleKeyEntity) pair.get(1)))
                .flatMap(pair ->
                        listener
                                .beforeUpdate(pair.getValue(), localeId, value)
                                .doOnNext(ValidationException::throwIfFailed)
                                .thenReturn(pair)
                )
                .map(pair -> updateTranslation(pair.getValue(), localeId, mapToNullIfEmpty(value), pair.getLeft()))
                .flatMap(translationRepository::save)
                .flatMap(bundleKey ->
                        listener.afterUpdate(bundleKey, bundleKey.getTranslationOrDie(localeId))
                                .thenReturn(bundleKey)
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

        final BundleFileEntity bundleFileEntity = new BundleFileEntity(bundleFile);
        workspaceEntity.addFile(bundleFileEntity);

        return Flux
                .fromIterable(bundleFile.getFiles())
                .map(ScannedBundleFileEntry::getLocale)
                .flatMap(locale ->
                        handler
                                .scanTranslations(bundleFileEntity.toLocation(), locale, context)
                                .index()
                                .map(indexedTranslation ->
                                        createTranslation(
                                                workspaceEntity,
                                                bundleFileEntity,
                                                locale.getId(),
                                                indexedTranslation.getT1(), // index
                                                indexedTranslation.getT2().getKey(), // bundle key
                                                indexedTranslation.getT2().getValue() // translation
                                        )
                                )
                )
                .groupBy(BundleKeyGroupKey::new)
                .flatMap(bundleKeyGroup ->
                        bundleKeyGroup
                                .collectList()
                                .map(groupedBundleKeys -> mergeAll(bundleKeyGroup.key(), groupedBundleKeys))
                )
                .flatMap(translationRepository::save)
                .then(Mono.just(bundleFileEntity));
    }

    /**
     * Creates a new {@link BundleKeyEntity bundle key entity} for a single translation.
     */
    private BundleKeyEntity createTranslation(WorkspaceEntity workspaceEntity,
                                              BundleFileEntity bundleFileEntity,
                                              String locale,
                                              long index,
                                              String bundleKey,
                                              String translation) {
        return new BundleKeyEntity(
                workspaceEntity.getId(),
                bundleFileEntity.getId(),
                bundleKey,
                new BundleKeyTranslationEntity(locale, index, mapToNullIfEmpty(translation))
        );
    }

    /**
     * Finds the {@link BundleKeyEntity bundle key} having the specified id.
     */
    private Mono<BundleKeyEntity> findBundleKeyOrDie(String bundleKeyId) {
        return translationRepository
                .findById(bundleKeyId)
                .switchIfEmpty(Mono.error(ResourceNotFoundException.translationNotFoundException(bundleKeyId)));
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
     * Merges all {@link BundleKeyEntity bundle keys} (one per translation) into a single one.
     */
    private BundleKeyEntity mergeAll(BundleKeyGroupKey key, List<BundleKeyEntity> groupedBundleKeys) {
        if (key == null) {
            throw new IllegalStateException("The grouping key is null.");
        }

        final BundleKeyEntity entity = new BundleKeyEntity(key.getWorkspace(), key.getBundleFile(), key.getKey());
        groupedBundleKeys.forEach(entity::addAllTranslations);

        return entity;
    }

    /**
     * Updates the translation of the specified {@link BundleKeyEntity bundle key} using the new updated value.
     */
    private BundleKeyEntity updateTranslation(BundleKeyEntity bundleKey, String localeId, String newUpdatedValue, String currentUser) {
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
     * Key grouping bundle keys.
     */
    @Data
    private static final class BundleKeyGroupKey {
        private final String workspace;
        private final String bundleFile;
        private final String key;

        public BundleKeyGroupKey(BundleKeyEntity bundleKey) {
            this.workspace = bundleKey.getWorkspace();
            this.bundleFile = bundleKey.getBundleFile();
            this.key = bundleKey.getKey();
        }
    }
}
