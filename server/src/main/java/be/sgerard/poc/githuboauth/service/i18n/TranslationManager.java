package be.sgerard.poc.githuboauth.service.i18n;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeysPageDto;
import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeysPageRequestDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryAPI;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
public interface TranslationManager {

    Collection<Locale> getRegisteredLocales();

    void readTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException;

    void writeTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException;

    BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) throws ResourceNotFoundException;

    void updateTranslations(String workspaceId, Map<String, String> translations) throws ResourceNotFoundException;
}
