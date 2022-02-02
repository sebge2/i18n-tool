package be.sgerard.i18n.service.translator.listener;

import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import reactor.core.publisher.Mono;

/**
 * Listener of {@link ExternalTranslatorConfigEntity translator config}.
 *
 * @author Sebastien Gerard
 */
public interface ExternalTranslatorConfigListener<C extends ExternalTranslatorConfigEntity> {

    /**
     * Checks that the specified config is supported.
     */
    boolean support(ExternalTranslatorConfigEntity config);

    /**
     * Performs an action after the creation of the specified translator config.
     */
    default Mono<Void> afterPersist(ExternalTranslatorConfigEntity config) {
        return Mono.empty();
    }

    /**
     * Performs an action after the update of the specified translator config.
     */
    default Mono<Void> afterUpdate(ExternalTranslatorConfigEntity config) {
        return Mono.empty();
    }

    /**
     * Performs an action after the deletion of the specified translator config.
     */
    default Mono<Void> afterDelete(ExternalTranslatorConfigEntity config) {
        return Mono.empty();
    }
}
