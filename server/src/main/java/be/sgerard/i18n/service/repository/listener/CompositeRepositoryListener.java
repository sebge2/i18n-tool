package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

import static be.sgerard.i18n.model.validation.ValidationResult.toValidationResult;

/**
 * Composite {@link RepositoryListener repository listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeRepositoryListener implements RepositoryListener<RepositoryEntity> {

    private final List<RepositoryListener<RepositoryEntity>> listeners;

    @SuppressWarnings("unchecked")
    public CompositeRepositoryListener(List<RepositoryListener<?>> listeners) {
        this.listeners = (List<RepositoryListener<RepositoryEntity>>) (List<?>) listeners;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public ValidationResult beforePersist(RepositoryEntity repository) {
        return listeners.stream()
                .filter(listener -> listener.support(repository))
                .map(listener -> listener.beforePersist(repository))
                .collect(toValidationResult());
    }

    @Override
    public void onCreate(RepositoryEntity repository) {
        listeners.stream()
                .filter(listener -> listener.support(repository))
                .forEach(listener -> listener.onCreate(repository));
    }

    @Override
    public ValidationResult beforeUpdate(RepositoryEntity original, RepositoryPatchDto patch) {
        return listeners.stream()
                .filter(listener -> listener.support(original))
                .map(listener -> listener.beforeUpdate(original, patch))
                .collect(toValidationResult());
    }

    @Override
    public void onUpdate(RepositoryEntity repository) {
        listeners.stream()
                .filter(listener -> listener.support(repository))
                .forEach(listener -> listener.onUpdate(repository));
    }

    @Override
    public ValidationResult beforeDelete(RepositoryEntity repository) {
        return listeners.stream()
                .filter(listener -> listener.support(repository))
                .map(listener -> listener.beforeDelete(repository))
                .collect(toValidationResult());
    }

    @Override
    public void onDelete(RepositoryEntity repository) {
        listeners.stream()
                .filter(listener -> listener.support(repository))
                .forEach(listener -> listener.onCreate(repository));
    }
}
