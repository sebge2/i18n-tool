package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.i18n.TranslationSearchManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * {@link RestController Controller} handling translations.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "Translation", description = "Controller handling translations.")
public class TranslationController {

    private final TranslationManager translationManager;
    private final TranslationSearchManager translationSearchManager;

    public TranslationController(TranslationManager translationManager, TranslationSearchManager translationSearchManager) {
        this.translationManager = translationManager;
        this.translationSearchManager = translationSearchManager;
    }

    /**
     * Returns the translation having the specified id.
     */
    @GetMapping(path = "/translation/{id}")
    @Operation(summary = "Returns the translation having the specified id.")
    public Mono<BundleKeyTranslationDto> findById(@PathVariable String id) {
        return translationManager
                .findTranslationOrDie(id)
                .map(translation -> BundleKeyTranslationDto.builder(translation).build());
    }

    /**
     * Performs the specified {@link TranslationsSearchRequestDto search request} and returns a page of {@link TranslationsPageDto translations}.
     */
    @PostMapping(path = "/translation/do", params = "action=search")
    @Operation(summary = "Returns translations of the workspace having the specified id.")
    public Mono<TranslationsPageDto> getWorkspaceTranslations(@RequestBody TranslationsSearchRequestDto searchRequest) {
        return translationSearchManager.search(searchRequest);
    }

    /**
     * Updates translations. The maps associated {@link BundleKeyTranslationDto#getId() translation ids} to their translations.
     */
    @PatchMapping(path = "/translation")
    @Operation(summary = "Updates translations of the workspace having the specified id.")
    public Flux<BundleKeyTranslationDto> updateWorkspaceTranslations(@RequestBody Map<String, String> translations) {
        return translationManager
                .updateTranslations(translations)
                .map(key -> BundleKeyTranslationDto.builder(key).build());
    }
}
