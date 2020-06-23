package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;

import java.util.stream.Stream;

/**
 * Custom {@link BundleKeyTranslationRepository translation repository}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepositoryCustom {

    /**
     * Searches {@link BundleKeyTranslationEntity translations} satisfying the specified {@link TranslationsSearchRequest request}.
     */
    Stream<BundleKeyTranslationEntity> search(TranslationsSearchRequest request);
}
