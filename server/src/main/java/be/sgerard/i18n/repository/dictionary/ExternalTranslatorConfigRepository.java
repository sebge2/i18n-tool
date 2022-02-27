package be.sgerard.i18n.repository.dictionary;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository Repository} of {@link ExternalTranslatorConfigEntity configuration entities}.
 */
public interface ExternalTranslatorConfigRepository extends ReactiveMongoRepository<ExternalTranslatorConfigEntity, String> {
}
