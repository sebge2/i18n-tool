package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyTranslationEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
@Repository
public interface BundleKeyEntryRepository extends CrudRepository<BundleKeyTranslationEntity, String>, BundleKeyEntryRepositoryCustom {

    @Query(value = "select distinct locale from translation_bundle_key_entry", nativeQuery = true)
    Collection<String> findAllLocales();
}
