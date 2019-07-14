package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeyEntrySearchRequestDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.BundleKeyEntryEntity;

import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
public interface BundleKeyEntryRepositoryCustom {

    Stream<BundleKeyEntryEntity> searchEntries(BundleKeyEntrySearchRequestDto request);
}
