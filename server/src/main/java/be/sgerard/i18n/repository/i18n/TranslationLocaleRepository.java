package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link TranslationLocaleEntity translation locale entities}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleRepository extends ReactiveMongoRepository<TranslationLocaleEntity, String> {
}
