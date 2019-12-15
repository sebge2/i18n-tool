package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

/**
 * Custom {@link BundleKeyEntityRepository bundle keys repository}.
 *
 * @author Sebastien Gerard
 */
public interface BundleKeyEntityRepositoryCustom {

    /**
     * Searches {@link BundleKeyEntity bundle keys} satisfying the specified {@link TranslationsSearchRequest request}.
     */
    Flux<BundleKeyEntity> search(TranslationsSearchRequest request);

    /**
     * Searches {@link BundleKeyEntity bundle keys} satisfying the specified {@link Query query}.
     */
    Flux<BundleKeyEntity> search(Query query);
}
