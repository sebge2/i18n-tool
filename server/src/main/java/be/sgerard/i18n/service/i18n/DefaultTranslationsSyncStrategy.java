package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Default implementation of {@link TranslationsSyncStrategy synchronization strategy}.
 * <p>
 * <ul>
 *  <li>If a translation has been updated locally, then the update is preserved.</li>
 *  <li>Indexes are updated.</li>
 *  <li>If the translation is no more defined in the remote repository, it's deleted locally.</li>
 *  <li>If the local updated translations is the same as the translation coming from the remote repository,
 *  it's no more updated and because de facto the current value.</li>
 * </ul>
 *
 * @author Sebastien Gerard
 */
public class DefaultTranslationsSyncStrategy implements TranslationsSyncStrategy {

    private final BundleKeyEntityRepository repository;

    public DefaultTranslationsSyncStrategy(BundleKeyEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Mono<BundleKeyEntity> synchronizeLocalAndRemote(BundleKeyEntity local, BundleKeyEntity remote) {
        for (Map.Entry<String, BundleKeyTranslationEntity> remoteEntry : remote.getTranslations().entrySet()) {
            final BundleKeyTranslationEntity remoteTranslation = remoteEntry.getValue();

            if (local.getTranslations().containsKey(remoteEntry.getKey())) {
                final BundleKeyTranslationEntity localTranslation = local.getTranslationOrDie(remoteEntry.getKey());

                localTranslation.setIndex(remoteTranslation.getIndex());
                localTranslation.setOriginalValue(remoteTranslation.getOriginalValue().orElse(null));
            } else {
                local.getTranslations().put(remoteEntry.getKey(), remoteTranslation);
            }
        }

        return repository.save(local);
    }

    @Override
    public Mono<BundleKeyEntity> synchronizeOnlyRemote(BundleKeyEntity remote) {
        // the translation is only present in the remote repository, but not locally
        return repository.save(remote);
    }

    @Override
    public Mono<BundleKeyEntity> synchronizeOnlyLocal(BundleKeyEntity local) {
        // the translation is only present locally (i.e., not available remotely)
        return repository.delete(local)
                .then(Mono.empty());
    }
}
