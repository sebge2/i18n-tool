package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

/**
 * Custom {@link BundleKeyTranslationRepository translation repository}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepositoryCustom {

    /**
     * Searches {@link BundleKeyTranslationEntity translations} satisfying the specified {@link TranslationsSearchRequest request}.
     */
    Flux<BundleKeyTranslationEntity> search(TranslationsSearchRequest request);

    /**
     * Searches {@link BundleKeyTranslationEntity translations} satisfying the specified {@link Query query}.
     */
    Flux<BundleKeyTranslationEntity> search(Query query);
}
