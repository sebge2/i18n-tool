package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeyEntrySearchRequestDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyTranslationEntity;

import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface BundleKeyEntryRepositoryCustom {

    Stream<BundleKeyTranslationEntity> searchEntries(BundleKeyEntrySearchRequestDto request);
}
