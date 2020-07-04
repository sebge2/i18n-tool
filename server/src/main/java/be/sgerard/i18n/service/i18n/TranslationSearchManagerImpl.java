package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageRowDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.GroupedFlux;
import reactor.core.publisher.Mono;

import java.util.List;

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
                                .map(translation -> BundleKeyTranslationDto.builder(translation).build())
                                .groupBy(BundleKeyTranslationDto::getBundleKey)
                                .flatMap(this::createRow)
                                .collectList()
                                .map(this::createPage)
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
                .lastKey(searchRequest.getLastKey().orElse(null))
                .build();
    }

    /**
     * Creates the {@link TranslationsPageRowDto row} for the grouped flow by bundle key.
     */
    private Mono<TranslationsPageRowDto> createRow(GroupedFlux<String, BundleKeyTranslationDto> group) {
        return group
                .collectList()
                .map(translations -> TranslationsPageRowDto.builder()
                        .bundleKey(group.key())
                        .translations(translations)
                        .build()
                );
    }

    /**
     * Creates the {@link TranslationsPageDto page} with the specified rows.
     */
    private TranslationsPageDto createPage(List<TranslationsPageRowDto> rows) {
        return TranslationsPageDto.builder()
                .lastKey(rows.isEmpty() ? null : rows.get(rows.size() - 1).getBundleKey())
                .rows(rows)
                .build();
    }
}
