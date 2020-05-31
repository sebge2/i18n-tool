package be.sgerard.i18n.service.workspace.strategy;

import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.service.repository.RepositoryException;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link WorkspaceTranslationsStrategy strategy}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeWorkspaceTranslationsStrategy implements WorkspaceTranslationsStrategy {

    private final List<WorkspaceTranslationsStrategy> strategies;

    public CompositeWorkspaceTranslationsStrategy(List<WorkspaceTranslationsStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public boolean support(RepositoryEntity repository) {
        return strategies.stream()
                .anyMatch(strategy -> strategy.support(repository));
    }

    @Override
    public Flux<String> listBranches(RepositoryEntity repository) throws RepositoryException {
        return strategies.stream()
                .filter(strategy -> strategy.support(repository))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported repository [" + repository + "]. Please make sure that all strategies have been registered."))
                .listBranches(repository);
    }

    @Override
    public Mono<Boolean> isReviewFinished(WorkspaceEntity workspace) {
        return null;
    }

    @Override
    public Mono<WorkspaceEntity> onInitialize(WorkspaceEntity workspace) {
        return strategies.stream()
                .filter(strategy -> strategy.support(workspace.getRepository()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported workspace [" + workspace + "]. Please make sure that all strategies have been registered."))
                .onInitialize(workspace);
    }

    @Override
    public Mono<WorkspaceEntity> onPublish(WorkspaceEntity workspace, String message) {
        return strategies.stream()
                .filter(strategy -> strategy.support(workspace.getRepository()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported workspace [" + workspace + "]. Please make sure that all strategies have been registered."))
                .onPublish(workspace, message);
    }

    @Override
    public Mono<WorkspaceEntity> onDelete(WorkspaceEntity workspace) {
        return strategies.stream()
                .filter(strategy -> strategy.support(workspace.getRepository()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported workspace [" + workspace + "]. Please make sure that all strategies have been registered."))
                .onDelete(workspace);
    }
}
