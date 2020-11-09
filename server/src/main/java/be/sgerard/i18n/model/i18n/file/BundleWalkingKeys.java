package be.sgerard.i18n.model.i18n.file;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Collection of {@link BundleKeyEntity bundle keys} found when walking around bundles.
 *
 * @author Sebastien Gerard
 */
public final class BundleWalkingKeys {

    private final Map<String, BundleKeyEntity> bundleKeys = new HashMap<>();

    public BundleWalkingKeys() {
    }

    public BundleWalkingKeys(Collection<BundleKeyEntity> bundleKeys) {
        for (BundleKeyEntity bundleKey : bundleKeys) {
            this.bundleKeys.put(bundleKey.getKey(), bundleKey);
        }
    }

    /**
     * Returns or creates if it does not exist, the {@link BundleKeyEntity bundle key} associated to the specified key.
     */
    public BundleKeyEntity getOrCreate(WorkspaceEntity workspace, BundleFileEntity bundleFile, String key) {
        BundleKeyEntity bundleKey = bundleKeys.get(key);

        if (bundleKey == null) {
            bundleKey = new BundleKeyEntity(workspace.getId(), bundleFile.getId(), key);

            bundleKeys.put(key, bundleKey);
        }

        return bundleKey;
    }

    /**
     * Returns the number of keys composing this object.
     */
    public long getNumberKeys(){
        return bundleKeys.size();
    }

    /**
     * Streams all {@link BundleKeyEntity bundle keys}.
     */
    public Flux<BundleKeyEntity> stream() {
        return Flux.fromIterable(bundleKeys.values());
    }
}
