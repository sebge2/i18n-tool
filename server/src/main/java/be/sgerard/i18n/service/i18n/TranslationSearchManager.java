package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsPageDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsSearchRequestDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Mono;

/**
 * Manager responsible of searching for translations.
 *
 * @author Sebastien Gerard
 */
public interface TranslationSearchManager {

    /**
     * Performs the specified {@link TranslationsSearchRequestDto search request} and returns a page of {@link TranslationsPageDto translations}.
     */
    Mono<TranslationsPageDto> search(TranslationsSearchRequestDto searchRequest) throws ResourceNotFoundException;

    /**
     * Returns whether some translations satisfy the specified request.
     */
    Mono<Boolean> hasElements(TranslationsSearchRequestDto searchRequest) throws ResourceNotFoundException;

}
