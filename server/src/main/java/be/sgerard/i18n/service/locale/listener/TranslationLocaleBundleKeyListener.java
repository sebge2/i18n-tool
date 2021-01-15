package be.sgerard.i18n.service.locale.listener;

import be.sgerard.i18n.model.locale.persistence.TranslationLocaleEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link TranslationLocaleListener Listener} that removes all locales from bundle keys.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationLocaleBundleKeyListener implements TranslationLocaleListener {

    private final BundleKeyEntityRepository repository;

    public TranslationLocaleBundleKeyListener(BundleKeyEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<Void> beforeDelete(TranslationLocaleEntity locale) {
        return repository.findAll()
                .doOnNext(bundleKey -> bundleKey.getTranslations().remove(locale.getId()))
                .flatMap(repository::save)
                .then();
    }
}
