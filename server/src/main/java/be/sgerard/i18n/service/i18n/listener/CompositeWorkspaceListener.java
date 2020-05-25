package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Composite {@link WorkspaceListener workspace listener}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeWorkspaceListener implements WorkspaceListener {

    private final List<WorkspaceListener> listeners;

    public CompositeWorkspaceListener(List<WorkspaceListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public void onCreate(WorkspaceEntity workspace) {
        listeners.stream()
                .filter(listener -> listener.support(workspace))
                .forEach(listener -> listener.onCreate(workspace));
    }

    @Override
    public ValidationResult beforeInitialize(WorkspaceEntity workspace) {
        return listeners.stream()
                .filter(listener -> listener.support(workspace))
                .map(listener -> listener.beforeInitialize(workspace))
                .collect(ValidationResult.toValidationResult());
    }

    @Override
    public void onInitialize(WorkspaceEntity workspace) {
        listeners.stream()
                .filter(listener -> listener.support(workspace))
                .forEach(listener -> listener.onInitialize(workspace));
    }

    @Override
    public ValidationResult beforePublish(WorkspaceEntity workspace) {
        return listeners.stream()
                .filter(listener -> listener.support(workspace))
                .map(listener -> listener.beforePublish(workspace))
                .collect(ValidationResult.toValidationResult());
    }

    @Override
    public ValidationResult beforeFinishReview(WorkspaceEntity workspace) {
        return listeners.stream()
                .filter(listener -> listener.support(workspace))
                .map(listener -> listener.beforeFinishReview(workspace))
                .collect(ValidationResult.toValidationResult());
    }

    @Override
    public void onDelete(WorkspaceEntity workspace) {
        listeners.stream()
                .filter(listener -> listener.support(workspace))
                .forEach(listener -> listener.onDelete(workspace));
    }
}
