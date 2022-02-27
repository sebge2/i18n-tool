package be.sgerard.i18n.repository.dictionary;

import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

/**
 * {@link ReactiveMongoRepository} of {@link DictionaryEntryEntity dictionary entry entities}.
 *
 * @author Sebastien Gerard
 */
public interface DictionaryEntryRepository extends ReactiveMongoRepository<DictionaryEntryEntity, String>, DictionaryEntryRepositoryCustom {
}