package be.sgerard.i18n.model.scheduler;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Optional;

/**
 * Request searching for {@link ScheduledTaskDefinition task definitions}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskDefinitionSearchRequest {

    /**
     * Request searching for all scheduled task definitions.
     */
    public static ScheduledTaskDefinitionSearchRequest requestForScheduledDefinitions(){
        return ScheduledTaskDefinitionSearchRequest.builder()
                .enabled(true)
                .build();
    }

    private final Boolean enabled;
    private final Instant executedBeforeOrEqualThan;
    private final Instant executedAfterOrEqualThan;

    /**
     * @see #enabled
     */
    public Optional<Boolean> isEnabled() {
        return Optional.ofNullable(enabled);
    }

    /**
     * @see #executedBeforeOrEqualThan
     */
    public Optional<Instant> getExecutedBeforeOrEqualThan() {
        return Optional.ofNullable(executedBeforeOrEqualThan);
    }

    /**
     * @see #executedAfterOrEqualThan
     */
    public Optional<Instant> getExecutedAfterOrEqualThan() {
        return Optional.ofNullable(executedAfterOrEqualThan);
    }
}
