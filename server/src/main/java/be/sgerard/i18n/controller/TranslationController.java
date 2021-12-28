package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationUpdateDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto;
import be.sgerard.i18n.service.i18n.text.TextTranslationManager;
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

import java.util.List;

import static java.util.stream.Collectors.toList;

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
    private final TextTranslationManager translateManager;

    public TranslationController(TranslationManager translationManager,
                                 TranslationSearchManager translationSearchManager,
                                 TextTranslationManager translateManager) {
        this.translationManager = translationManager;
        this.translationSearchManager = translationSearchManager;
        this.translateManager = translateManager;
    }

    /**
     * Performs the specified {@link TranslationsSearchRequestDto search request} and returns a page of {@link TranslationsPageDto translations}.
     */
    @PostMapping(path = "/translation/do", params = "action=search")
    @Operation(
            operationId = "searchTranslations",
            summary = "Returns translations of the workspace having the specified id.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "search"))
    )
    public Mono<TranslationsPageDto> searchTranslations(@RequestBody TranslationsSearchRequestDto request) {
        return translationSearchManager.search(request);
    }

    /**
     * Translates a text in another language.
     */
    @PostMapping(path = "/translation/text/do", params = "action=translateText")
    @Operation(
            operationId = "translateText",
            summary = "Translates a text in another language.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "translateText"))
    )
    public Mono<TextTranslationResponseDto> translate(@RequestBody TextTranslationRequestDto request) {
        return translateManager.translate(request);
    }

    /**
     * Updates a particular {@link TranslationDto translation}.
     */
    @PutMapping(path = "/translation/bundle-key/{bundleKeyId}/locale/{localeId}", consumes = MediaType.TEXT_PLAIN_VALUE)
    @Operation(operationId = "updateTranslation", summary = "Updates a particular translation.")
    public Mono<TranslationDto> updateTranslation(@RequestBody(required = false) String translation,
                                                  @PathVariable String bundleKeyId,
                                                  @PathVariable String localeId) {
        return translationManager
                .updateTranslation(
                        TranslationUpdateDto.builder()
                                .bundleKeyId(bundleKeyId)
                                .localeId(localeId)
                                .translation(translation)
                                .build()
                )
                .map(bundleKeyTranslation -> TranslationDto.builder(bundleKeyTranslation).build());
    }

    /**
     * Updates the specified {@link TranslationDto translations} and returns them.
     */
    @PutMapping(path = "/translation")
    @Operation(operationId = "updateTranslations", summary = "Updates translations.")
    public Mono<List<TranslationDto>> updateTranslations(@RequestBody(required = false) List<TranslationUpdateDto> translations) {
        return translationManager
                .updateTranslations(translations)
                .map(updatedTranslations ->
                        updatedTranslations.stream()
                                .map(updatedTranslation -> TranslationDto.builder(updatedTranslation).build())
                                .collect(toList())
                );
    }
}
