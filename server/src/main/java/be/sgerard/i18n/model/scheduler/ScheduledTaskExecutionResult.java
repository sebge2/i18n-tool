package be.sgerard.i18n.model.scheduler;

import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Optional;

/**
 * Result of scheduled task execution.
 *
 * @author Sebastien Gerard
 */
@Getter
@Builder(builderClassName = "Builder")
public class ScheduledTaskExecutionResult {

    /**
     * Status at the end of the execution.
     */
    private final ScheduledTaskExecutionStatus status;

    /**
     * Short description (characters are truncated if there is not enough place on the screen) of the result to be displayed to the end-user.
     */
    private final LocalizedString shortDescription;

    /**
     * Description of the result to be displayed to the end-user. If no description is provided, the short description is used. The description may contain HTML.
     */
    private final LocalizedString description;

    /**
     * @see #description
     */
    public Optional<LocalizedString> getDescription() {
        return Optional.ofNullable(description);
    }

    /**
     * Builder of {@link ScheduledTaskExecutionResult execution results}.
     */
    public static final class Builder {

        @SuppressWarnings({"unused", "FieldMayBeFinal"})
        private ScheduledTaskExecutionStatus status = ScheduledTaskExecutionStatus.SUCCESSFUL;

    }

}
