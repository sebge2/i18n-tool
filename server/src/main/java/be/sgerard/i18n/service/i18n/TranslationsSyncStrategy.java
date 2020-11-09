package be.sgerard.i18n.service.i18n;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Mono;

/**
 * Synchronize the state of translations based on what's coming from the local database and the remote repository.
 *
 * @author Sebastien Gerard
 */
public interface TranslationsSyncStrategy {

    /**
     * Synchronizes the state of a {@link BundleKeyEntity translation} stored locally and coming from the remote repository.
     */
    Mono<BundleKeyEntity> synchronizeLocalAndRemote(BundleKeyEntity local, BundleKeyEntity remote);

    /**
     * Synchronizes the state of a {@link BundleKeyEntity translation} only coming from the remote repository.
     */
    Mono<BundleKeyEntity> synchronizeOnlyRemote(BundleKeyEntity remote);

    /**
     * Synchronizes the state of a {@link BundleKeyEntity translation} only stored locally.
     */
    Mono<BundleKeyEntity> synchronizeOnlyLocal(BundleKeyEntity local);

    /**
     * Synchronizes the state of a {@link BundleKeyEntity translation} stored locally and coming from the remote repository.
     */
    default Mono<BundleKeyEntity> synchronizeLocalAndRemote(Pair<BundleKeyEntity, BundleKeyEntity> localAndRemote){
        if(localAndRemote.getLeft() != null){
            if(localAndRemote.getRight() != null){
                return synchronizeLocalAndRemote(localAndRemote.getLeft(), localAndRemote.getRight());
            } else {
                return synchronizeOnlyLocal(localAndRemote.getLeft());
            }
        } else {
            return synchronizeOnlyRemote(localAndRemote.getRight());
        }
    }
}
