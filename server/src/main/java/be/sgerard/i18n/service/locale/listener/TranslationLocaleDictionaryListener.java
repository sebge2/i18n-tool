package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.service.dictionary.DictionaryManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link TranslationLocaleListener Listener} applied when deleting a locale. Dictionary entries will be impacted accordingly.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleDictionaryListener implements TranslationLocaleListener {

    private final DictionaryManager dictionaryManager;

    public TranslationLocaleDictionaryListener(DictionaryManager dictionaryManager) {
        this.dictionaryManager = dictionaryManager;
    }

    @Override
    public Mono<Void> beforeDelete(TranslationLocaleEntity locale) {
        return dictionaryManager.findAll()
                .filter(entry -> entry.getTranslations().containsKey(locale.getId()))
                .doOnNext(entry -> entry.getTranslations().remove(locale.getId()))
                .flatMap(dictionaryManager::update)
                .then();
    }
}
