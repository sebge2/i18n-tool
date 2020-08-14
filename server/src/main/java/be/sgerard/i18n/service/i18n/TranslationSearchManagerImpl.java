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
import be.sgerard.i18n.service.security.auth.AuthenticationUserManager;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

import static be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository.*;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;

/**
 * Implementation of the {@link TranslationSearchManager search manager}.
 *
 * @author Sebastien Gerard
 */
@Service
public class TranslationSearchManagerImpl implements TranslationSearchManager {

    private final BundleKeyTranslationRepository keyEntryRepository;
    private final TranslationLocaleManager localeManager;
    private final AuthenticationUserManager authenticationUserManager;
    private final WorkspaceManager workspaceManager;

    public TranslationSearchManagerImpl(BundleKeyTranslationRepository keyEntryRepository,
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
                                .map(translations -> createPage(createRows(translations, request), request))
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
                                .criterion(searchRequest.getCriterion())
                                .keyPattern(searchRequest.getKeyPattern().orElse(null))
                                .maxTranslations(searchRequest.getMaxKeys() * locales.size())
                                .pageIndex(searchRequest.getPageIndex())
                                .sortBy(asList(FIELD_WORKSPACE, FIELD_BUNDLE_FILE, FIELD_BUNDLE_KEY))
                                .currentUser(currentUser.getUserId())
                                .build()
                );
    }

    /**
     * Returns the {@link FoundTranslationGroupKey key} of this translation.
     */
    private FoundTranslationGroupKey toGroupKey(BundleKeyTranslationEntity translation) {
        return new FoundTranslationGroupKey(translation.getWorkspace(), translation.getBundleFile(), translation.getBundleKey());
    }

    /**
     * Creates the {@link TranslationsPageRowDto rows} for {@link BundleKeyTranslationEntity translations} by grouping them.
     */
    private List<TranslationsPageRowDto> createRows(List<BundleKeyTranslationEntity> translations, TranslationsSearchRequest request) {
        return translations
                .stream()
                .collect(Collectors.groupingBy(this::toGroupKey, LinkedHashMap::new, toList()))
                .entrySet()
                .stream()
                .map(group -> createRow(group, request))
                .collect(toList());
    }

    /**
     * Creates the {@link TranslationsPageRowDto row} for the grouped flow by bundle key.
     */
    private TranslationsPageRowDto createRow(Map.Entry<FoundTranslationGroupKey, List<BundleKeyTranslationEntity>> group,
                                             TranslationsSearchRequest searchRequest) {
        final FoundTranslationGroupKey key = group.getKey();

        final List<BundleKeyTranslationEntity> orderedTranslations = new ArrayList<>(group.getValue());
        orderedTranslations.sort(Comparator.comparingInt(translation -> searchRequest.getLocales().indexOf(translation.getLocale())));

        return TranslationsPageRowDto.builder()
                .workspace(key.getWorkspace())
                .bundleFile(key.getBundleFile())
                .bundleKey(key.getBundleKey())
                .translations(orderedTranslations.stream().map(translation -> TranslationsPageTranslationDto.builder(translation).build()).collect(toList()))
                .build();
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
