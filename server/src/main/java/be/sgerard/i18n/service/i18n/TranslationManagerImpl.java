package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.i18n.BundleType;
import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.i18n.model.i18n.file.BundleWalkContext;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFile;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKey;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.security.user.dto.UserDto;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.i18n.file.BundleHandler;
import be.sgerard.i18n.service.i18n.file.BundleWalker;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static be.sgerard.i18n.model.event.EventType.UPDATED_TRANSLATIONS;
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

    private final WorkspaceRepository workspaceRepository;
    private final BundleKeyTranslationRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationController authenticationManager;
    private final EventService eventService;
    private final BundleWalker walker;
    private final List<BundleHandler> handlers;

    public TranslationManagerImpl(WorkspaceRepository workspaceRepository,
                                  BundleKeyTranslationRepository keyEntryRepository,
                                  TranslationLocaleManager localeManager,
                                  AuthenticationController authenticationManager,
                                  EventService eventService,
                                  List<BundleHandler> handlers) {
        this.workspaceRepository = workspaceRepository;
        this.keyEntryRepository = keyEntryRepository;
        this.localeManager = localeManager;
        this.authenticationManager = authenticationManager;
        this.eventService = eventService;
        this.walker = new BundleWalker(handlers);
        this.handlers = handlers;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<BundleKeysPageDto> getTranslations(BundleKeysPageRequestDto searchRequest) {
        final WorkspaceEntity workspaceEntity = workspaceRepository.findById(searchRequest.getWorkspaceId())
                .orElseThrow(() -> ResourceNotFoundException.workspaceNotFoundException(searchRequest.getWorkspaceId()));

        final GroupedTranslations groupedEntries = doGetTranslations(searchRequest);

        return Mono.just(
                BundleKeysPageDto.builder()
                        .workspaceId(workspaceEntity.getId())
                        .files(
                                groupedEntries.getGroups().entrySet().stream()
                                        .map(file ->
                                                BundleFileDto.builder()
                                                        .id(file.getKey().getId())
                                                        .name(file.getKey().getName())
                                                        .location(file.getKey().getLocation())
                                                        .keys(
                                                                file.getValue().entrySet().stream()
                                                                        .map(key ->
                                                                                BundleKeyDto.builder()
                                                                                        .id(key.getKey().getId())
                                                                                        .key(key.getKey().getKey())
                                                                                        .translations(
                                                                                                key.getValue().stream()
                                                                                                        .map(keyEntry ->
                                                                                                                BundleKeyTranslationDto.builder()
                                                                                                                        .id(keyEntry.getId())
                                                                                                                        .locale(keyEntry.getLocale())
                                                                                                                        .originalValue(keyEntry.getOriginalValue().orElse(null))
                                                                                                                        .updatedValue(keyEntry.getUpdatedValue().orElse(null))
                                                                                                                        .lastEditor(keyEntry.getLastEditor().orElse(null))
                                                                                                                        .build()
                                                                                                        )
                                                                                                        .collect(toList())
                                                                                        )
                                                                                        .build()
                                                                        )
                                                                        .collect(toList())
                                                        )
                                                        .build()
                                        )
                                        .collect(toList())
                        )
                        .lastKey(groupedEntries.getLastKey().orElse(null))
                        .build()
        );
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

                    return getHandler(bundleFile).updateBundle(bundleFile, getTranslations(file), api);
                })
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> updateTranslations(WorkspaceEntity workspace, Map<String, String> translations) throws ResourceNotFoundException {
        // TODO
        final UserDto currentUser = authenticationManager.getCurrentUser().getUser();

        return Flux
                .fromIterable(translations.entrySet())
                .flatMap(entry ->
                        findTranslation(entry.getKey())
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
                .map(TranslationLocaleEntity::toLocale)
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
        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(
                        workspaceEntity,
                        bundleFile.getName(),
                        bundleFile.getLocationDirectory().toString(),
                        bundleFile.getType(),
                        bundleFile.getLocales(),
                        bundleFile.getFiles().stream().map(File::toString).collect(toList())
                );

                    groupedTranslations.getGroups().get(bundleFile).get(bundleKey).add(entryEntity);
                });

        return groupedTranslations;
    }

    private BundleHandler getHandler(ScannedBundleFile bundleFile) {
        for (BundleHandler handler : handlers) {

            if (handler.support(bundleFile.getType())) {
                return handler;
            }
        }

        throw new IllegalStateException("There is no handler supporting [" + bundleFile + "].");
    }

    /**
     * Finds the {@link BundleKeyTranslationEntity translation} having the specified id.
     */
    private Mono<BundleKeyTranslationEntity> findTranslation(String id) {
        return Mono
                .justOrEmpty(keyEntryRepository.findById(id))
                .switchIfEmpty(Mono.error(ResourceNotFoundException.translationNotFoundException(id)));
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
                                                .map(keyEntryEntity ->
                                                        Pair.of(keyEntryEntity.getJavaLocale(), keyEntryEntity.getValue().orElse(null))
                                                )
                                                .collect(HashMap::new, (m, v) -> m.put(v.getKey(), v.getValue()), HashMap::putAll)
                                )
                )
                .collect(toList());
    }

    private static final class GroupedTranslations {

        private final Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyTranslationEntity>>> groups = new LinkedHashMap<>();
        private String lastKey;
        private int numberEntries;

        public GroupedTranslations() {
        }

        public Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyTranslationEntity>>> getGroups() {
            return groups;
        }

        public Optional<String> getLastKey() {
            return Optional.ofNullable(lastKey);
        }

        public void setLastKey(String lastKey) {
            this.lastKey = lastKey;
        }

        public int getNumberEntries() {
            return numberEntries;
        }

        public void incrementNumberEntries() {
            numberEntries++;
        }
    }

}
