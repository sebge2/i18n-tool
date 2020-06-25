package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.*;
import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.repository.workspace.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;

import static java.util.stream.Collectors.toList;

/**
 * Implementation of the {@link TranslationSearchManager search manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationSearchManagerImpl implements TranslationSearchManager {

    private final BundleKeyTranslationRepository keyEntryRepository;
    private final WorkspaceRepository workspaceRepository;

    public TranslationSearchManagerImpl(BundleKeyTranslationRepository keyEntryRepository, WorkspaceRepository workspaceRepository) {
        this.keyEntryRepository = keyEntryRepository;
        this.workspaceRepository = workspaceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TranslationsPageDto> search(TranslationsSearchRequestDto searchRequest) {
        final WorkspaceEntity workspaceEntity = workspaceRepository
                .findAll().iterator().next();
//                .findById(searchRequest.getWorkspaces())
//                .orElseThrow(() -> ResourceNotFoundException.workspaceNotFoundException(searchRequest.getWorkspaces()));

        final GroupedTranslations groupedEntries = doGetTranslations(searchRequest);

        return Mono.just(
                TranslationsPageDto.builder()
                        .workspaces(
                              TranslationsWorkspaceDto.builder()
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
                                                                                                                                      .locale(keyEntry.getLocale().getId())
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
                                      .build()
                        )
//                        .workspaceId(workspaceEntity.getId())
//                        .files(

//                        )
                        .lastKey(groupedEntries.getLastKey().orElse(null))
                        .build()
        );
    }

    private GroupedTranslations doGetTranslations(TranslationsSearchRequestDto searchRequest) {
        final GroupedTranslations groupedTranslations = new GroupedTranslations();

        keyEntryRepository
                .search(
                        TranslationsSearchRequest.builder()
//                                .locales(searchRequest.getLocales())
                                .criterion(searchRequest.getCriterion())
//                                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                                .lastKey(searchRequest.getLastKey().orElse(null))
                                .maxKeyEntries(Integer.MAX_VALUE)
//                                .maxKeyEntries(searchRequest.getMaxKeys() * localeManager.findAll().size()) TODO
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
