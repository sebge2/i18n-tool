package be.sgerard.i18n.service.translator;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorGenericRestConfigEntity;

/**
 * All possible type of {@link ExternalTranslatorConfigEntity translator config types}.
 */
public enum ExternalTranslatorConfigType {

    /**
     * @see ExternalTranslatorGenericRestConfigEntity
     */
    EXTERNAL_GENERIC_REST,

}
