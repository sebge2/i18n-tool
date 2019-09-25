package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.dto.BundleKeysPageDto;
import be.sgerard.i18n.model.i18n.dto.BundleKeysPageRequestDto;
import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.ResourceNotFoundException;
import be.sgerard.i18n.service.git.RepositoryAPI;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sebastien Gerard
 */
public interface TranslationManager {

    Collection<Locale> getLocales();

    void readTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException;

    void writeTranslations(WorkspaceEntity workspaceEntity, RepositoryAPI api) throws IOException;

    BundleKeysPageDto getTranslations(BundleKeysPageRequestDto searchRequest) throws ResourceNotFoundException;

    void updateTranslations(WorkspaceEntity workspaceEntity, Map<String, String> translations) throws ResourceNotFoundException;
}
