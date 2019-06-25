package be.sgerard.i18n.service.i18n.persistence;

import be.sgerard.i18n.model.i18n.dto.BundleKeyEntrySearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;

import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface BundleKeyTranslationRepositoryCustom {

    Stream<BundleKeyTranslationEntity> searchEntries(BundleKeyEntrySearchRequestDto request);
}
