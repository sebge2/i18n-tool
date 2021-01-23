package be.sgerard.i18n.service.scheduler.executor;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toSet;

/**
 * Composite {@link StaticScheduledTaskProvider static scheduled task provider}.
 *
 * @author Sebastien Gerard
 */
@Component
@Primary
public class CompositeStaticScheduledTaskProvider implements StaticScheduledTaskProvider {

    private final List<StaticScheduledTaskProvider> providers;

    @Lazy
    public CompositeStaticScheduledTaskProvider(@Autowired(required = false) List<StaticScheduledTaskProvider> providers) {
        this.providers = providers;
    }

    @Override
    public Collection<ScheduledTaskDefinition> getTaskDefinitions() {
        return providers.stream()
                .flatMap(provider -> provider.getTaskDefinitions().stream())
                .collect(toSet());
    }
}
