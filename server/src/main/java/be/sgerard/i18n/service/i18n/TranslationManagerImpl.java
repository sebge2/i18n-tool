package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.controller.AuthenticationController;
import be.sgerard.i18n.model.event.EventType;
import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.i18n.model.i18n.dto.TranslationsUpdateEventDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileDto;
import be.sgerard.i18n.model.i18n.file.ScannedBundleFileKeyDto;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.model.security.user.UserDto;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.event.EventService;
import be.sgerard.i18n.service.event.InternalEventListener;
import be.sgerard.i18n.service.repository.git.GitAPI;
import be.sgerard.i18n.service.i18n.file.TranslationBundleHandler;
import be.sgerard.i18n.service.i18n.file.TranslationBundleWalker;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.repository.i18n.WorkspaceRepository;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.*;

import static be.sgerard.i18n.model.event.EventType.UPDATED_TRANSLATIONS;
import static be.sgerard.i18n.model.event.EventType.UPDATED_TRANSLATION_LOCALE;
import static be.sgerard.i18n.service.i18n.file.TranslationFileUtils.mapToNullIfEmpty;
import static java.util.stream.Collectors.toList;

/**
 * @author Sebastien Gerard
 */
@Service
public class TranslationManagerImpl implements TranslationManager {

    private final AppProperties properties;
    private final WorkspaceRepository workspaceRepository;
    private final BundleKeyTranslationRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationController authenticationManager;
    private final EventService eventService;
    private final TranslationBundleWalker walker;
    private final List<TranslationBundleHandler> handlers;

    public TranslationManagerImpl(AppProperties properties,
                                  WorkspaceRepository workspaceRepository,
                                  BundleKeyTranslationRepository keyEntryRepository,
                                  TranslationLocaleManager localeManager,
                                  AuthenticationController authenticationManager,
                                  EventService eventService,
                                  List<TranslationBundleHandler> handlers) {
        this.properties = properties;
        this.workspaceRepository = workspaceRepository;
        this.keyEntryRepository = keyEntryRepository;
        this.localeManager = localeManager;
        this.authenticationManager = authenticationManager;
        this.eventService = eventService;
        this.walker = new TranslationBundleWalker(handlers);
        this.handlers = handlers;
    }

    @Override
    @Transactional(readOnly = true)
    public BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) {
        final WorkspaceEntity workspaceEntity = workspaceRepository.findById(searchRequest.getWorkspaceId())
                .orElseThrow(() -> ResourceNotFoundException.workspaceNotFoundException(searchRequest.getWorkspaceId()));

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
                .build();
    }

    @Override
    @Transactional
    public void readTranslations(WorkspaceEntity workspaceEntity, GitAPI api) throws IOException {
        walker.walk(api, (bundleFile, entries) -> onBundleFound(workspaceEntity, bundleFile, entries));
    }

    @Override
    @Transactional
    public void writeTranslations(WorkspaceEntity workspaceEntity, GitAPI api) throws IOException {
        for (BundleFileEntity file : workspaceEntity.getFiles()) {
            final ScannedBundleFileDto bundleFile = new ScannedBundleFileDto(file);

            getHandler(bundleFile).updateBundle(bundleFile, getTranslations(file), api);
        }
    }

    @Override
    @Transactional
    public void updateTranslations(WorkspaceEntity workspace, Map<String, String> translations) throws ResourceNotFoundException {
        final UserDto currentUser = authenticationManager.getCurrentUser().getUser();

        final List<BundleKeyTranslationDto> updatedEntries = new ArrayList<>();
        for (Map.Entry<String, String> updateEntry : translations.entrySet()) {
            final BundleKeyTranslationEntity entry = keyEntryRepository.findById(updateEntry.getKey())
                    .orElseThrow(() -> ResourceNotFoundException.translationNotFoundException(updateEntry.getKey()));

            if (!Objects.equals(workspace.getId(), entry.getBundleKey().getBundleFile().getWorkspace().getId())) {
                throw new IllegalArgumentException("The entry [" + entry.getId() + "] does not belong to the workspace [" + workspace.getId() + "].");
            }

            entry.setUpdatedValue(mapToNullIfEmpty(updateEntry.getValue()));

            if (Objects.equals(entry.getOriginalValue(), entry.getUpdatedValue())) {
                entry.setUpdatedValue(null);
            }

            entry.setLastEditor(entry.getUpdatedValue().isEmpty() ? null : currentUser.getId());
            updatedEntries.add(BundleKeyTranslationDto.builder().build());
        }

        eventService.broadcastEvent(
                UPDATED_TRANSLATIONS,
                new TranslationsUpdateEventDto(
                        WorkspaceDto.builder(workspace).build(),
                        currentUser,
                        updatedEntries
                )
        );
    }

    private Collection<Locale> getLocales() {
//        return properties.getLocales().keySet(); TODO
        return new HashSet<>();
    }

    private GroupedTranslations doGetTranslations(BundleKeysPageRequestDto searchRequest) {
        final GroupedTranslations groupedTranslations = new GroupedTranslations();

        keyEntryRepository
                .searchEntries(
                        BundleKeyEntrySearchRequestDto.builder(searchRequest.getWorkspaceId())
                                .locales(searchRequest.getLocales())
                                .criterion(searchRequest.getCriterion())
                                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                                .lastKey(searchRequest.getLastKey().orElse(null))
                                .maxKeyEntries(searchRequest.getMaxKeys() * localeManager.findAll().size())
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
                               List<ScannedBundleFileKeyDto> keys) {
        final BundleFileEntity bundleFileEntity =
                new BundleFileEntity(
                        workspaceEntity,
                        bundleFile.getName(),
                        bundleFile.getLocationDirectory().toString(),
                        bundleFile.getType(),
                        bundleFile.getLocales(),
                        bundleFile.getFiles().stream().map(File::toString).collect(toList())
                );

        keys.forEach(
                entry -> {
                    final BundleKeyEntity keyEntity = new BundleKeyEntity(bundleFileEntity, entry.getKey());

                    for (Locale locale : getLocales()) {
                        new BundleKeyTranslationEntity(keyEntity, locale.toLanguageTag(), mapToNullIfEmpty(entry.getTranslations().get(locale)));
                    }
                });
    }

    private List<ScannedBundleFileKeyDto> getTranslations(BundleFileEntity file) {
        return file.getKeys().stream()
                .map(
                        keyEntity ->
                                new ScannedBundleFileKeyDto(
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

    // TODO
    private static final class Listener implements InternalEventListener<TranslationLocaleDto>{

        @Override
        public boolean support(EventType eventType) {
            return eventType == UPDATED_TRANSLATION_LOCALE;
        }

        @Override
        public void onEvent(TranslationLocaleDto event) {

        }
    }
}
