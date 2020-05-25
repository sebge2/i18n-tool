package be.sgerard.i18n.service.i18n.review;

import be.sgerard.i18n.model.i18n.persistence.WorkspaceEntity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Composite {@link WorkspaceReviewHandler workspace review handler}.
 *
 * @author Sebastien Gerard
 */
@Primary
@Component
public class CompositeWorkspaceReviewHandler implements WorkspaceReviewHandler {

    private final List<WorkspaceReviewHandler> reviewHandlers;

    public CompositeWorkspaceReviewHandler(List<WorkspaceReviewHandler> reviewHandlers) {
        this.reviewHandlers = reviewHandlers;
    }

    @Override
    public boolean support(WorkspaceEntity workspace) {
        return true;
    }

    @Override
    public boolean isReviewFinished(WorkspaceEntity workspace) {
        return reviewHandlers.stream()
                .filter(handler -> handler.support(workspace))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported workspace [" + workspace + "]."))
                .isReviewFinished(workspace);
    }

    @Override
    public boolean isReviewSupported(WorkspaceEntity workspace) {
        return reviewHandlers.stream()
                .filter(handler -> handler.support(workspace))
                .findFirst()
                .map(listener -> listener.isReviewSupported(workspace))
                .orElse(false);
    }
}
