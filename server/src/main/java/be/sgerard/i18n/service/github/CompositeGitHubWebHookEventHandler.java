package be.sgerard.i18n.service.github;

import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.github.external.BaseGitHubWebHookEventDto;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Composite {@link GitHubWebHookEventHandler event handler}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeGitHubWebHookEventHandler implements GitHubWebHookEventHandler<BaseGitHubWebHookEventDto> {

    private final List<GitHubWebHookEventHandler<BaseGitHubWebHookEventDto>> handlers;

    @SuppressWarnings("unchecked")
    public CompositeGitHubWebHookEventHandler(List<GitHubWebHookEventHandler<?>> handlers) {
        this.handlers = (List<GitHubWebHookEventHandler<BaseGitHubWebHookEventDto>>) (List<?>) handlers;
    }

    @Override
    public boolean support(BaseGitHubWebHookEventDto event) {
        return handlers.stream().anyMatch(handler -> handler.support(event));
    }

    @Override
    public Mono<Void> handle(GitHubRepositoryEntity repository, BaseGitHubWebHookEventDto event) {
        return handlers.stream()
                .filter(handler -> handler.support(event))
                .findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported event [" + event + "]. Hint: check that all handlers have been registered."))
                .handle(repository, event);
    }
}
