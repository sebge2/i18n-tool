package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.controller.AuthenticationController;
import be.sgerard.poc.githuboauth.model.i18n.dto.*;
import be.sgerard.poc.githuboauth.model.i18n.event.TranslationsUpdateEventDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.poc.githuboauth.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleFileEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntryEntity;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.model.security.user.UserDto;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.event.EventService;
import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationBundleHandler;
import be.sgerard.poc.githuboauth.service.i18n.file.TranslationBundleWalker;
import be.sgerard.poc.githuboauth.service.i18n.persistence.BundleKeyEntryRepository;
import be.sgerard.poc.githuboauth.service.i18n.persistence.WorkspaceRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static be.sgerard.poc.githuboauth.model.event.Events.EVENT_UPDATED_TRANSLATIONS;
import static be.sgerard.poc.githuboauth.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * @author Sebastien Gerard
 */
@Service
public class TranslationManagerImpl implements TranslationManager {

    private final WorkspaceRepository workspaceRepository;
    private final BundleKeyEntryRepository keyEntryRepository;
    private final AuthenticationController authenticationManager;
    private final EventService eventService;
    private final TranslationBundleWalker walker;
    private final List<TranslationBundleHandler> handlers;

    public TranslationManagerImpl(WorkspaceRepository workspaceRepository,
                                  BundleKeyEntryRepository keyEntryRepository,
                                  AuthenticationController authenticationManager,
                                  EventService eventService,
                                  List<TranslationBundleHandler> handlers) {
        this.workspaceRepository = workspaceRepository;
        this.keyEntryRepository = keyEntryRepository;
        this.authenticationManager = authenticationManager;
        this.eventService = eventService;
        this.walker = new TranslationBundleWalker(handlers);
        this.handlers = handlers;
    }

    @Override
    @Transactional(readOnly = true)
    public BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) {
        final WorkspaceEntity workspaceEntity = workspaceRepository.findById(searchRequest.getWorkspaceId())
                .orElseThrow(() -> new ResourceNotFoundException(searchRequest.getWorkspaceId()));

        final GroupedTranslations groupedEntries = doGetTranslations(searchRequest);

        return BundleKeysPageDto.builder()
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
                                                                                .entries(
                                                                                        key.getValue().stream()
                                                                                                .map(keyEntry ->
                                                                                                        BundleKeyEntryDto.builder()
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
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Locale> getRegisteredLocales() {
        return keyEntryRepository.findAllLocales().stream().map(Locale::forLanguageTag).collect(toList());
    }

    @Override
    @Transactional
    public void readTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException {
        walker.walk(api, (bundleFile, entries) -> onBundleFound(workspaceEntity, bundleFile, entries));
    }

    @Override
    @Transactional
    public void writeTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException {
        for (BundleFileEntity file : workspaceEntity.getFiles()) {
            final ScannedBundleFileDto bundleFile = new ScannedBundleFileDto(file);

            final TranslationBundleHandler handler = getHandler(bundleFile);

            handler.updateBundle(
                    bundleFile,
                    () -> getTranslations(file),
                    api);
        }
    }

    @Override
    @Transactional
    public void updateTranslations(WorkspaceEntity workspace, Map<String, String> translations) throws ResourceNotFoundException {
        final UserDto currentUser = authenticationManager.getCurrentUser();

        final List<BundleKeyEntryDto> updatedEntries = new ArrayList<>();
        for (Map.Entry<String, String> updateEntry : translations.entrySet()) {
            final BundleKeyEntryEntity entry = keyEntryRepository.findById(updateEntry.getKey())
                    .orElseThrow(() -> new ResourceNotFoundException(updateEntry.getKey()));

            if (!Objects.equals(workspace.getId(), entry.getBundleKey().getBundleFile().getWorkspace().getId())) {
                throw new IllegalArgumentException("The entry [" + entry.getId() + "] does not belong to the workspace [" + workspace.getId() + "].");
            }

            entry.setLastEditor(currentUser.getId());
            entry.setUpdatedValue(mapToNullIfEmpty(updateEntry.getValue()));
            updatedEntries.add(BundleKeyEntryDto.builder().build());
        }

        eventService.broadcastEvent(
                EVENT_UPDATED_TRANSLATIONS,
                new TranslationsUpdateEventDto(
                        WorkspaceDto.builder(workspace).build(),
                        currentUser,
                        updatedEntries
                )
        );
    }

    private GroupedTranslations doGetTranslations(BundleKeysPageRequestDto searchRequest) {
        final GroupedTranslations groupedTranslations = new GroupedTranslations();

        keyEntryRepository
                .searchEntries(
                        BundleKeyEntrySearchRequestDto.builder(searchRequest.getWorkspaceId())
                                .missingLocales(searchRequest.getMissingLocales())
                                .locales(searchRequest.getLocales())
                                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                                .hasBeenUpdated(searchRequest.hasBeenUpdated().orElse(null))
                                .lastKey(searchRequest.getLastKey().orElse(null))
                                .maxKeyEntries(searchRequest.getMaxKeys() * getRegisteredLocales().size())
                                .build()
                )
                .filter(entryEntity -> groupedTranslations.getNumberEntries() <= searchRequest.getMaxKeys())
                .forEach(entryEntity -> {
                    final BundleKeyEntity bundleKey = entryEntity.getBundleKey();
                    final BundleFileEntity bundleFile = bundleKey.getBundleFile();

                    groupedTranslations.getGroups().putIfAbsent(bundleFile, new LinkedHashMap<>());

                    if (!groupedTranslations.getGroups().get(bundleFile).containsKey(bundleKey)) {
                        groupedTranslations.incrementNumberEntries();
                        groupedTranslations.setLastKey(bundleKey.getId());

                        groupedTranslations.getGroups().get(bundleFile).putIfAbsent(bundleKey, new ArrayList<>());
                    }

                    groupedTranslations.getGroups().get(bundleFile).get(bundleKey).add(entryEntity);
                });

        return groupedTranslations;
    }

    private TranslationBundleHandler getHandler(ScannedBundleFileDto bundleFile) {
        for (TranslationBundleHandler handler : handlers) {

            if (handler.support(bundleFile)) {
                return handler;
            }
        }

        throw new IllegalStateException("There is no handler supporting [" + bundleFile + "].");
    }

    private void onBundleFound(WorkspaceEntity workspaceEntity,
                               ScannedBundleFileDto bundleFile,
                               Stream<ScannedBundleFileKeyDto> entries) {
        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(
                        workspaceEntity,
                        bundleFile.getName(),
                        bundleFile.getLocationDirectory().toString(),
                        bundleFile.getType(),
                        bundleFile.getFiles().stream().map(File::toString).collect(toList())
                );

        entries.forEach(
                entry -> {
                    final BundleKeyEntity keyEntity = new BundleKeyEntity(bundleFileEntity, entry.getKey());

                    for (Map.Entry<Locale, String> translationEntry : entry.getTranslations().entrySet()) {
                        new BundleKeyEntryEntity(keyEntity, translationEntry.getKey().toLanguageTag(), translationEntry.getValue());
                    }
                });
    }

    private Stream<ScannedBundleFileKeyDto> getTranslations(BundleFileEntity file) {
        return file.getKeys().stream()
                .map(
                        keyEntity ->
                                new ScannedBundleFileKeyDto(
                                        keyEntity.getKey(),
                                        keyEntity.getEntries().stream()
                                                .map(keyEntryEntity ->
                                                        Pair.of(keyEntryEntity.getJavaLocale(), keyEntryEntity.getValue().orElse(null))
                                                )
                                                .collect(toMap(Pair::getKey, Pair::getValue))
                                )
                );
    }

    private static final class GroupedTranslations {

        private final Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyEntryEntity>>> groups = new LinkedHashMap<>();
        private String lastKey;
        private int numberEntries;

        public GroupedTranslations() {
        }

        public Map<BundleFileEntity, Map<BundleKeyEntity, List<BundleKeyEntryEntity>>> getGroups() {
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
