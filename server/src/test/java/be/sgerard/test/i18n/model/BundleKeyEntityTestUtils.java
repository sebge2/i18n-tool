package be.sgerard.test.i18n.model;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;

import java.util.HashMap;
import java.util.Map;

import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.ENGLISH_ID;
import static be.sgerard.test.i18n.model.TranslationLocaleEntityTestUtils.FRANCAIS_ID;
import static be.sgerard.test.i18n.model.UserEntityTestUtils.JOHN_DOE_ID;
import static be.sgerard.test.i18n.model.WorkspaceEntityTestUtils.*;

/**
 * @author Sebastien Gerard
 */
public final class BundleKeyEntityTestUtils {

    public static final String DEVELOP_ACCESS_DENIED_EXCEPTION = "1a03cd49-3952-421c-8954-d0c5076dab0a";
    public static final String DEVELOP_WORKSPACES_TITLE = "ba1391ee-093e-4e52-b9d9-535468cb7fad";

    private BundleKeyEntityTestUtils() {
    }

    public static BundleKeyEntity accessDeniedDevelopGitHub() {
        final Map<String, BundleKeyTranslationEntity> translations = new HashMap<>();
        translations.put(
                ENGLISH_ID,
                new BundleKeyTranslationEntity(ENGLISH_ID, "The access is denied.", 0)
                        .setModification(new BundleKeyTranslationModificationEntity("The access is denied. Check your access.", JOHN_DOE_ID))
        );
        translations.put(FRANCAIS_ID, new BundleKeyTranslationEntity(FRANCAIS_ID, "L'accès n'est pas permis.", 0));

        return new BundleKeyEntity(DEVELOP_I18N_TOOL_GITHUB_ID, DEVELOP_EXCEPTIONS_FILE_I18N_TOOL_GITHUB_ID, "AccessDeniedException.message")
                .setId(DEVELOP_ACCESS_DENIED_EXCEPTION)
                .setTranslations(translations);
    }

    public static BundleKeyEntity workspacesTitleDevelopGitHub() {
        final Map<String, BundleKeyTranslationEntity> translations = new HashMap<>();
        translations.put(ENGLISH_ID, new BundleKeyTranslationEntity(ENGLISH_ID, "Workspaces", 0));
        translations.put(FRANCAIS_ID, new BundleKeyTranslationEntity(FRANCAIS_ID, "Espaces de Travail", 0));

        return new BundleKeyEntity(DEVELOP_I18N_TOOL_GITHUB_ID, DEVELOP_I18N_FILE_I18N_TOOL_GITHUB_ID, "SHARED.WORKSPACES_TITLE")
                .setId(DEVELOP_WORKSPACES_TITLE)
                .setTranslations(translations);
    }
}
