package be.sgerard.i18n.service.i18n.text;

import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationRequestDto;
import be.sgerard.i18n.model.i18n.dto.translation.text.TextTranslationResponseDto;
import reactor.core.publisher.Mono;

/**
 * Manager dealing with translation of text.
 */
public interface TextTranslationManager {

    /**
     * Translates a text in another language.
     */
    Mono<TextTranslationResponseDto> translate(TextTranslationRequestDto request);
}
