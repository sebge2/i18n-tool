package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.i18n.TranslationSearchManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
     * Performs the specified {@link TranslationsSearchRequestDto search request} and returns a page of {@link TranslationsPageDto translations}.
     */
    @PostMapping(path = "/translation/do", params = "action=search")
    @Operation(
            summary = "Returns translations of the workspace having the specified id.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "search"))
    )
    public Mono<TranslationsPageDto> searchTranslations(@RequestBody TranslationsSearchRequestDto searchRequest) {
        return translationSearchManager.search(searchRequest);
    }

    /**
     * Updates translations. The maps associated {@link TranslationDto#getId() translation ids} to their translations.
     */
    @PatchMapping(path = "/translation/bundle-key/{bundleKeyId}/locale/{localeId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    @Operation(summary = "Updates translations of the workspace having the specified id.")
    public Mono<TranslationDto> updateWorkspaceTranslations(@RequestBody(required = false) String translation,
                                                            @PathVariable String bundleKeyId,
                                                            @PathVariable String localeId) {
        return translationManager
                .updateTranslation(bundleKeyId, localeId, translation)
                .map(bundleKey -> bundleKey.getTranslationOrDie(localeId))
                .map(key -> TranslationDto.builder(key).build());
    }
}
