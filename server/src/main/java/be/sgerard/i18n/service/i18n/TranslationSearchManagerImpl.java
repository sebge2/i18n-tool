package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageRowDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.Comparator;
import java.util.List;

import static be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository.*;

/**
 * Implementation of the {@link TranslationSearchManager search manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationSearchManagerImpl implements TranslationSearchManager {

    private final BundleKeyTranslationRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final WorkspaceManager workspaceManager;

    public TranslationSearchManagerImpl(BundleKeyTranslationRepository keyEntryRepository,
                                        TranslationLocaleManager localeManager,
                                        WorkspaceManager workspaceManager) {
        this.keyEntryRepository = keyEntryRepository;
        this.localeManager = localeManager;
        this.workspaceManager = workspaceManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TranslationsPageDto> search(TranslationsSearchRequestDto searchRequest) {
        return Mono
                .zip(getWorkspaces(searchRequest), getLocales(searchRequest))
                .map(input -> createRequest(searchRequest, input.getT1(), input.getT2()))
                .flatMap(request ->
                        keyEntryRepository
                                .search(request)
                                .groupBy(this::toGroupKey)
                                .flatMap(groupedTranslation -> createRow(groupedTranslation, request))
                                .collectList()
                                .map(rows -> createPage(rows, request))
                );
    }

    /**
     * Returns {@link WorkspaceEntity workspaces} involved in the request.
     */
    private Mono<List<String>> getWorkspaces(TranslationsSearchRequestDto searchRequest) {
        if (searchRequest.getWorkspaces().isEmpty()) {
            return workspaceManager.findAll().map(WorkspaceEntity::getId).collectList();
        } else {
            return Mono.just(searchRequest.getWorkspaces());
        }
    }

    /**
     * Returns {@link TranslationLocaleEntity locales} involved in the request.
     */
    private Mono<List<String>> getLocales(TranslationsSearchRequestDto searchRequest) {
        if (searchRequest.getLocales().isEmpty()) {
            return localeManager.findAll().map(TranslationLocaleEntity::getId).collectList();
        } else {
            return Mono.just(searchRequest.getLocales());
        }
    }

    /**
     * Creates the {@link TranslationsSearchRequest request} based on the current workspaces and locales.
     */
    private TranslationsSearchRequest createRequest(TranslationsSearchRequestDto searchRequest,
                                                    List<String> workspaces,
                                                    List<String> locales) {
        return TranslationsSearchRequest.builder()
                .workspaces(workspaces)
                .locales(locales)
                .criterion(searchRequest.getCriterion())
                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                .maxTranslations(searchRequest.getMaxKeys() * locales.size())
                .pageIndex(searchRequest.getPageIndex())
                .sortBy(FIELD_WORKSPACE, FIELD_BUNDLE_FILE, FIELD_BUNDLE_KEY)
                .build();
    }

    /**
     * Returns the {@link FoundTranslationGroupKey key} of this translation.
     */
    private FoundTranslationGroupKey toGroupKey(BundleKeyTranslationEntity translation) {
        return new FoundTranslationGroupKey(translation.getWorkspace(), translation.getBundleFile(), translation.getBundleKey());
    }

    /**
     * Creates the {@link TranslationsPageRowDto row} for the grouped flow by bundle key.
     */
    private Mono<TranslationsPageRowDto> createRow(GroupedFlux<FoundTranslationGroupKey, BundleKeyTranslationEntity> group,
                                                   TranslationsSearchRequest searchRequest) {
        final FoundTranslationGroupKey key = group.key();

        if (key == null) {
            throw new IllegalStateException("The grouping key is null.");
        }

        return group
                .sort(Comparator.comparingInt(translation -> searchRequest.getLocales().indexOf(translation.getLocale())))
                .map(translation -> TranslationsPageTranslationDto.builder(translation).build())
                .collectList()
                .map(translations ->
                        TranslationsPageRowDto.builder()
                                .workspace(key.getWorkspace())
                                .bundleFile(key.getBundleFile())
                                .bundleKey(key.getBundleKey())
                                .translations(translations)
                                .build()
                );
    }

    /**
     * Creates the {@link TranslationsPageDto page} with the specified {@link TranslationsPageRowDto rows}.
     */
    private TranslationsPageDto createPage(List<TranslationsPageRowDto> rows, TranslationsSearchRequest searchRequest) {
        return TranslationsPageDto.builder()
                .pageIndex(searchRequest.getPageIndex())
                .rows(rows)
                .locales(searchRequest.getLocales())
                .build();
    }

    /**
     * Key grouping a {@link BundleKeyTranslationEntity translation} in a row.
     */
    @Getter
    @EqualsAndHashCode
    private static final class FoundTranslationGroupKey {

        private final String workspace;
        private final String bundleFile;
        private final String bundleKey;

        private FoundTranslationGroupKey(String workspace, String bundleFile, String bundleKey) {
            this.workspace = workspace;
            this.bundleFile = bundleFile;
            this.bundleKey = bundleKey;
        }
    }

}
