package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import reactor.core.publisher.Flux;

/**
 * Custom {@link BundleKeyTranslationRepository translation repository}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepositoryCustom {

    /**
     * Searches {@link BundleKeyTranslationEntity translations} satisfying the specified {@link TranslationsSearchRequestDto request}.
     */
    Flux<BundleKeyTranslationEntity> search(TranslationsSearchRequestDto request);
}
