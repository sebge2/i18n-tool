package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageRowDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Implementation of the {@link TranslationSearchManager search manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationSearchManagerImpl implements TranslationSearchManager {

    private final BundleKeyEntityRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationUserManager authenticationUserManager;
    private final WorkspaceManager workspaceManager;

    public TranslationSearchManagerImpl(BundleKeyEntityRepository keyEntryRepository,
                                        TranslationLocaleManager localeManager,
                                        AuthenticationUserManager authenticationUserManager,
                                        WorkspaceManager workspaceManager) {
        this.keyEntryRepository = keyEntryRepository;
        this.localeManager = localeManager;
        this.authenticationUserManager = authenticationUserManager;
        this.workspaceManager = workspaceManager;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TranslationsPageDto> search(TranslationsSearchRequestDto searchRequest) {
        return Mono
                .zip(getWorkspaces(searchRequest), getLocales(searchRequest))
                .flatMap(input -> createRequest(searchRequest, input.getT1(), input.getT2()))
                .flatMap(request ->
                        keyEntryRepository
                                .search(request)
                                .collectList()
                                .map(bundleKeys -> createPage(bundleKeys, request))
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
    private Mono<TranslationsSearchRequest> createRequest(TranslationsSearchRequestDto searchRequest,
                                                          List<String> workspaces,
                                                          List<String> locales) {
        return authenticationUserManager
                .getCurrentUserOrDie()
                .map(currentUser ->
                        TranslationsSearchRequest.builder()
                                .workspaces(workspaces)
                                .locales(locales)
                                .bundleFiles(searchRequest.getBundleFiles())
                                .criterion(searchRequest.getCriterion())
                                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                                .maxKeys(searchRequest.getMaxKeys())
                                .lastPageKey(searchRequest.getLastPageKey().orElse(null))
                                .currentUser(currentUser.getUserId())
                                .maxKeys(searchRequest.getMaxKeys())
                                .build()
                );
    }

    /**
     * Creates the {@link TranslationsPageRowDto rows} for {@link BundleKeyTranslationEntity translations} by grouping them.
     */
    private List<TranslationsPageRowDto> createRows(List<BundleKeyEntity> bundleKeys, TranslationsSearchRequest request) {
        return bundleKeys.stream()
                .map(group -> createRow(group, request))
                .collect(toList());
    }

    /**
     * Creates the {@link TranslationsPageRowDto row} for the bundle key.
     */
    private TranslationsPageRowDto createRow(BundleKeyEntity bundleKey, TranslationsSearchRequest searchRequest) {
        return TranslationsPageRowDto.builder()
                .id(bundleKey.getId())
                .workspace(bundleKey.getWorkspace())
                .bundleFile(bundleKey.getBundleFile())
                .bundleKey(bundleKey.getKey())
                .translations(
                        searchRequest.getLocales().stream()
                                .map(bundleKey::getTranslationOrCreate)
                                .map(translation -> TranslationsPageTranslationDto.builder(translation).build())
                                .collect(toList())
                )
                .build();
    }

    /**
     * Creates the {@link TranslationsPageDto page} containing translations of the specified {@link BundleKeyEntity bundle keys}.
     */
    private TranslationsPageDto createPage(List<BundleKeyEntity> bundleKeys, TranslationsSearchRequest request) {
        return TranslationsPageDto.builder()
                .rows(createRows(bundleKeys, request))
                .locales(request.getLocales())
                .lastPageKey(!bundleKeys.isEmpty() ? bundleKeys.get(bundleKeys.size() - 1).getSortingKey() : null)
                .build();
    }
}
