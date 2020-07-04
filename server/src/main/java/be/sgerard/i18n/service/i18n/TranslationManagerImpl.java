package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.model.i18n.file.ScannedBundleTranslation;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntryEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.user.dto.AuthenticatedUserDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.ValidationException;
import be.sgerard.i18n.service.i18n.file.BundleHandler;
import be.sgerard.i18n.service.i18n.file.BundleWalker;
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
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.Collections.emptyList;
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

    private final BundleKeyTranslationRepository translationRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationController authenticationManager;
    private final RepositoryManager repositoryManager;
    private final TranslationsListener listener;
    private final BundleWalker walker;
    private final List<BundleHandler> handlers;

    public TranslationManagerImpl(BundleKeyTranslationRepository translationRepository,
                                  TranslationLocaleManager localeManager,
                                  AuthenticationController authenticationManager,
                                  RepositoryManager repositoryManager, TranslationsListener listener,
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
    public Mono<BundleKeyTranslationEntity> findTranslation(String id) {
        return translationRepository.findById(id);
    }

    @Override
    @Transactional
    public Flux<BundleFileEntity> readTranslations(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return createContext(workspace, api)
                .flatMapMany(context -> walker.walk((bundleFile, entries) -> onBundleFound(workspace, bundleFile, entries, context), context));
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
        return authenticationManager
                .getCurrentUser()
                .map(AuthenticatedUserDto::getUser)
                .flatMapMany(currentUser ->
                        Flux
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
                                )
                );
    }

    /**
     * Creates the {@link BundleWalkContext context} used for walking around repository files.
     */
    private Mono<BundleWalkContext> createContext(WorkspaceEntity workspace, TranslationRepositoryReadApi api) {
        return Mono
                .zip(
                        localeManager.findAll().collectList(),
                        createInclusionPredicates(workspace)
                )
                .map(tuple -> new BundleWalkContext(api, tuple.getT2(), tuple.getT1()));
    }

    /**
     * Creates predicates that will return whether the specified path can be associated to bundle file having the specified type.
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
                                                 Flux<ScannedBundleTranslation> translations,
                                                 BundleWalkContext context) {
        logger.info("A bundle file has been found located in [{}] named [{}] with {} file(s).",
                bundleFile.getLocationDirectory(), bundleFile.getName(), bundleFile.getFiles().size());

        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(
                        bundleFile.getName(),
                        bundleFile.getLocationDirectory().toString(),
                        bundleFile.getType(),
                        bundleFile.getFiles().stream().map(BundleFileEntryEntity::new).collect(toList())
                );

        workspaceEntity.addFile(bundleFileEntity);// TODO save

        final SortedSet<String> keys = new TreeSet<>(String::compareTo);

        return translations
                .doOnNext(translation -> keys.add(translation.getKey()))
                .flatMap(translation -> createTranslation(workspaceEntity, bundleFileEntity, translation))
                .then(Mono.defer(() ->
                                ReactiveUtils
                                        .combine(
                                                generateExpectedTranslations(keys, context),
                                                findActualTranslations(bundleFileEntity),
                                                this::compareExpectedAndActual
                                        )
                                        .filter(matching -> matching.getRight() == null)
                                        .flatMap(matching ->
                                                createMissingTranslation(workspaceEntity, bundleFileEntity, matching.getLeft().getLeft(), matching.getLeft().getRight())
                                        )
                                        .then()
                        )
                )
                .then(Mono.just(bundleFileEntity));
    }

    /**
     * Creates and saves a new {@link BundleKeyTranslationRepository translation}.
     */
    private Mono<BundleKeyTranslationEntity> createTranslation(WorkspaceEntity workspaceEntity,
                                                               BundleFileEntity bundleFileEntity,
                                                               ScannedBundleTranslation translation) {
        return translationRepository.save(new BundleKeyTranslationEntity(
                workspaceEntity.getId(),
                bundleFileEntity.getId(),
                translation.getKey(),
                translation.getFileEntry().getLocale().getId(),
                mapToNullIfEmpty(translation.getValue().orElse(null))
        ));
    }

    /**
     * Creates and saves a new {@link BundleKeyTranslationRepository translation} which has not been found.
     */
    private Mono<BundleKeyTranslationEntity> createMissingTranslation(WorkspaceEntity workspaceEntity,
                                                                      BundleFileEntity bundleFileEntity,
                                                                      String bundleKey,
                                                                      TranslationLocaleEntity locale) {
        return translationRepository.save(new BundleKeyTranslationEntity(
                workspaceEntity.getId(),
                bundleFileEntity.getId(),
                bundleKey,
                locale.getId(),
                null
        ));
    }

    /**
     * Finds all the {@link BundleKeyTranslationRepository translations} of the specified bundle (whatever the language).
     */
    private Flux<BundleKeyTranslationEntity> findActualTranslations(BundleFileEntity bundleFile) {
        return translationRepository.search(
                new Query()
                        .addCriteria(Criteria.where(BundleKeyTranslationRepository.FIELD_BUNDLE_FILE).is(bundleFile.getId()))
                        .with(Sort.by(BundleKeyTranslationRepository.FIELD_BUNDLE_KEY, BundleKeyTranslationRepository.FIELD_LOCALE))
        );
    }

    /**
     * Generates a flux with all the expected translations (translation key for a locale).
     */
    private Flux<Pair<String, TranslationLocaleEntity>> generateExpectedTranslations(SortedSet<String> allBundleTranslationKeys, BundleWalkContext context) {
        final SortedSet<TranslationLocaleEntity> sortedLocales = new TreeSet<>(Comparator.comparing(TranslationLocaleEntity::getId));
        sortedLocales.addAll(context.getLocales());

        return Flux.fromIterable(allBundleTranslationKeys)
                .flatMap(key -> Flux.fromIterable(sortedLocales).map(locale -> Pair.of(key, locale)));
    }

    /**
     * Compares the bundle key with its locale to the specified {@link BundleKeyTranslationEntity translation}.
     */
    private Integer compareExpectedAndActual(Pair<String, TranslationLocaleEntity> expected, BundleKeyTranslationEntity actual) {
        final int keyComparison = expected.getKey().compareTo(actual.getBundleKey());

        if (keyComparison != 0) {
            return keyComparison;
        }

        return expected.getValue().getId().compareTo(actual.getLocale());
    }

    /**
     * Returns all the {@link ScannedBundleFileKey keys} composing the specified {@link BundleFileEntity bundle file}.
     */
    private List<ScannedBundleFileKey> getTranslations(BundleFileEntity file) {
        return emptyList();
//        return file.getKeys().stream()
//                .map(
//                        keyEntity ->
//                                new ScannedBundleFileKey(
//                                        keyEntity.getKey(),
//                                        keyEntity.getTranslations().stream()
//                                                .map(keyEntryEntity -> Pair.of(keyEntryEntity.getLocale(), keyEntryEntity.getValue().orElse(null)))
//                                                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll)
//                                )
//                )
//                .collect(toList());
    }
}
