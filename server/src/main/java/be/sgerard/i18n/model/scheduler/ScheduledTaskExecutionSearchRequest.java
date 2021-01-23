package be.sgerard.i18n.model.scheduler;

import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * Request searching for {@link ScheduledTaskExecution task executions}.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskExecutionSearchRequest {

    /**
     * Default value of the limit.
     */
    public static final int DEFAULT_LIMIT = 50;

    private final boolean ascending;
    private final Instant executedBeforeOrEqualThan;
    private final Instant executedAfterOrEqualThan;
    private final String taskDefinitionId;
    private final Collection<ScheduledTaskExecutionStatus> statuses;
    private final Integer limit;

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

    /**
     * @see #taskDefinitionId
     */
    public Optional<String> getTaskDefinitionId() {
        return Optional.ofNullable(taskDefinitionId);
    }

    /**
     * @see #limit
     */
    public Optional<Integer> getLimit() {
        return Optional.ofNullable(limit);
    }

    /**
     * Builder of {@link ScheduledTaskExecutionSearchRequest search requests}.
     */
    public static final class Builder {

        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        private List<ScheduledTaskExecutionStatus> statuses = asList(ScheduledTaskExecutionStatus.values());

        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        private int limit = DEFAULT_LIMIT;

    }

}
