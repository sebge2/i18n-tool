package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.persistence.TranslationLocaleEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;

/**
 * {@link CrudRepository Repository} of {@link TranslationLocaleEntity translation locale entities}.
 *
 * @author Sebastien Gerard
 */
public interface TranslationLocaleRepository extends CrudRepository<TranslationLocaleEntity, String> {

    @Override
    Collection<TranslationLocaleEntity> findAll();
}
