package be.sgerard.i18n.service.translator;

import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto;
import reactor.core.publisher.Mono;

/**
 * Service translating text using external sources.
 */
public interface ExternalTranslator {

    /**
     * Translates the text specified by the {@link TextTranslationRequestDto request} and returns {@link TextTranslationResponseDto translations}.
     */
    Mono<TextTranslationResponseDto> translate(TextTranslationRequestDto request);

}
