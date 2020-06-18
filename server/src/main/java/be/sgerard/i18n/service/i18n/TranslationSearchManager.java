package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.BundleKeysPageDto;
import be.sgerard.i18n.model.i18n.dto.BundleKeysPageRequestDto;
import be.sgerard.i18n.service.ResourceNotFoundException;
import reactor.core.publisher.Mono;

/**
 * Manager responsible of searching for translations.
 *
 * @author Sebastien Gerard
 */
public interface TranslationSearchManager {

    /**
     * Performs the specified {@link BundleKeysPageRequestDto search request} and returns a page of {@link BundleKeysPageDto translations}.
     */
    Mono<BundleKeysPageDto> getTranslations(BundleKeysPageRequestDto searchRequest) throws ResourceNotFoundException;

}
