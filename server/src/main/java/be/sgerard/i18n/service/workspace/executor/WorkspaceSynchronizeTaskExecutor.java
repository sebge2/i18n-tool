package be.sgerard.i18n.service.workspace.executor;

import be.sgerard.i18n.configuration.AppProperties;
import be.sgerard.i18n.model.core.localized.LocalizedString;
import be.sgerard.i18n.model.error.LocalizedMessagesHolder;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionResult;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.repository.RepositoryManager;
import be.sgerard.i18n.service.scheduler.executor.BaseStaticScheduledTaskExecutor;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import lombok.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import static be.sgerard.i18n.model.core.localized.LocalizedString.joiningLocalized;
import static be.sgerard.i18n.model.scheduler.ScheduledTaskDefinition.recurringTrigger;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.joining;

/**
 * {@link BaseStaticScheduledTaskExecutor Executor} of the synchronization of workspaces.
 *
 * @author Sebastien Gerard
 */
@Component
public class WorkspaceSynchronizeTaskExecutor extends BaseStaticScheduledTaskExecutor {

    public static final String TASK_ID = "workspace-synchronization";

    private final RepositoryManager repositoryManager;
    private final WorkspaceManager workspaceManager;

    public WorkspaceSynchronizeTaskExecutor(AppProperties appProperties,
                                            RepositoryManager repositoryManager,
                                            WorkspaceManager workspaceManager) {
        super(
                ScheduledTaskDefinition.builder()
                        .id(TASK_ID)
                        .name(LocalizedString.fromBundle("i18n/misc", "scheduled-task.workspace-synchronization.name"))
                        .description(LocalizedString.fromBundle("i18n/misc", "scheduled-task.workspace-synchronization.description"))
                        .trigger(recurringTrigger(appProperties.getRepository().getAutoSyncFrequency()))
                        .build()
        );

        this.repositoryManager = repositoryManager;
        this.workspaceManager = workspaceManager;
    }

    @Override
    protected Mono<ScheduledTaskExecutionResult> doExecuteTask() {
        return repositoryManager
                .findAll()
                .filter(RepositoryEntity::isAutoSynchronized)
                .flatMap(repository ->
                        workspaceManager
                                .findAll(repository.getId())
                                .filter(workspace -> workspace.getStatus() != WorkspaceStatus.NOT_INITIALIZED)
                                .flatMap(workspace -> synchronize(repository, workspace))
                )
                .collectList()
                .map(SyncResults::new)
                .map(this::createFinalResult);
    }

    /**
     * Synchronizes the specified {@link WorkspaceEntity workspace}.
     */
    private Mono<SyncResult> synchronize(RepositoryEntity repository, WorkspaceEntity workspace) {
        return workspaceManager
                .synchronize(workspace.getId())
                .map(wk -> new SyncResult(repository, wk, null))
                .onErrorResume(cause -> Mono.just(new SyncResult(repository, workspace, cause)));
    }

    /**
     * Creates the {@link ScheduledTaskExecutionResult execution result} based on {@link SyncResults synchronization results}.
     */
    private ScheduledTaskExecutionResult createFinalResult(SyncResults results) {
        return ScheduledTaskExecutionResult
                .builder()
                .status(results.getStatus())
                .shortDescription(createShortDescription(results))
                .description(createLongDescription(results))
                .build();
    }

    /**
     * Creates the short description specifying the number of successful and failed workspaces.
     */
    private LocalizedString createShortDescription(SyncResults results) {
        return LocalizedString.fromBundle(
                "i18n/misc",
                "scheduled-task.workspace-synchronization.result.short-description",
                results.getNumberSuccess(), results.getNumberFailures()
        );
    }

    /**
     * Creates the long description listing workspaces that failed and succeeded.
     */
    private LocalizedString createLongDescription(SyncResults results) {
        return Stream
                .of(
                        LocalizedString.fromBundle(
                                "i18n/misc",
                                "scheduled-task.workspace-synchronization.result.long-description.intro"
                        ),
                        createFailuresDescription(results),
                        createSuccessDescription(results)
                )
                .collect(joiningLocalized(joining()));
    }

    /**
     * Creates the success description part of the long description.
     */
    private LocalizedString createSuccessDescription(SyncResults results) {
        if (!results.hasSuccess()) {
            return LocalizedString.fromBundle(
                    "i18n/misc",
                    "scheduled-task.workspace-synchronization.result.long-description.no-success"
            );
        }

        return LocalizedString.fromBundle(
                "i18n/misc",
                "scheduled-task.workspace-synchronization.result.long-description.success",
                results.getSuccess().stream()
                        .flatMap(this::toListWorkspaceName)
                        .collect(joiningLocalized(joining()))
        );
    }

    /**
     * Creates the success description part of the long description.
     */
    private LocalizedString createFailuresDescription(SyncResults results) {
        if (!results.hasFailures()) {
            return LocalizedString.fromBundle(
                    "i18n/misc",
                    "scheduled-task.workspace-synchronization.result.long-description.no-failure"
            );
        }

        return LocalizedString.fromBundle(
                "i18n/misc",
                "scheduled-task.workspace-synchronization.result.long-description.failure",
                results.getFailures().stream()
                        .flatMap(this::toErrorEntry)
                        .collect(joiningLocalized(joining()))
        );
    }

    /**
     * Creates the list entry containing the workspace name (with its repository name).
     */
    private Stream<LocalizedString> toListWorkspaceName(SyncResult result) {
        return Stream.of(
                new LocalizedString(String.format(
                        "<li>%s - %s</li>",
                        result.getRepository().getName(),
                        result.workspace.getBranch()
                ))
        );
    }

    /**
     * Creates the list entry explaining the synchronization failure.
     */
    private Stream<LocalizedString> toErrorEntry(SyncResult failure) {
        if (failure.getCauseMessages().isPresent()) {
            return Stream.of(
                    toListWorkspaceName(failure),
                    Stream.of(new LocalizedString("<ul>")),
                    Stream.of(
                            failure.getCauseMessagesOrFallback().stream()
                                    .collect(joiningLocalized(joining("", "<li>", "</li>")))
                    ),
                    Stream.of(new LocalizedString("</ul>"))
            ).flatMap(Function.identity());
        } else {
            return toListWorkspaceName(failure);
        }
    }

    /**
     * Results of the synchronization of workspaces.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    private final static class SyncResult {

        /**
         * The {@link RepositoryEntity repository} of the workspace.
         */
        private final RepositoryEntity repository;

        /**
         * The synchronized {@link WorkspaceEntity workspace}.
         */
        private final WorkspaceEntity workspace;

        /**
         * The cause of the synchronization failure (if failed).
         */
        private final Throwable cause;

        /**
         * @see #cause
         */
        public Optional<Throwable> getCause() {
            return Optional.ofNullable(cause);
        }

        /**
         * Lists {@link LocalizedString localized messages} of the failure caused.
         */
        public Optional<List<LocalizedString>> getCauseMessages() {
            return getCause()
                    .filter(LocalizedMessagesHolder.class::isInstance)
                    .map(LocalizedMessagesHolder.class::cast)
                    .map(LocalizedMessagesHolder::toLocalizedMessages);
        }

        /**
         * Lists {@link LocalizedString localized messages} of the failure caused, or a fallback message saying that an internal exception occurred.
         */
        public List<LocalizedString> getCauseMessagesOrFallback() {
            return getCauseMessages()
                    .orElseGet(() -> singletonList(LocalizedString.fromBundle("i18n/exception", "InternalException.message")));
        }

        /**
         * Returns whether the synchronization failed.
         */
        public boolean isSuccessful() {
            return getCause().isEmpty();
        }
    }

    /**
     * Results of the synchronization of workspaces.
     */
    @Value
    @SuppressWarnings("RedundantModifiersValueLombok")
    private final static class SyncResults {

        /**
         * The successful results.
         */
        private final List<SyncResult> success;

        /**
         * The failed results.
         */
        private final List<SyncResult> failures;

        public SyncResults(List<SyncResult> results) {
            final List<SyncResult> successfulResults = new ArrayList<>();
            final List<SyncResult> failedResults = new ArrayList<>();

            for (SyncResult result : results) {
                if (result.isSuccessful()) {
                    successfulResults.add(result);
                } else {
                    failedResults.add(result);
                }
            }

            this.success = successfulResults;
            this.failures = failedResults;
        }

        /**
         * Returns the number of synchronization failures.
         */
        public int getNumberFailures() {
            return getFailures().size();
        }

        /**
         * Returns whether some synchronizations failed.
         */
        public boolean hasFailures() {
            return getNumberFailures() > 0;
        }

        /**
         * Returns whether some synchronizations succeeded.
         */
        public int getNumberSuccess() {
            return getSuccess().size();
        }

        /**
         * Returns whether some synchronizations succeeded.
         */
        public boolean hasSuccess() {
            return getNumberSuccess() > 0;
        }

        /**
         * Returns the number of synchronization success.
         */
        public int getNumberResults() {
            return getNumberFailures() + getNumberSuccess();
        }

        /**
         * Returns the {@link ScheduledTaskExecutionStatus status} of the execution.
         */
        public ScheduledTaskExecutionStatus getStatus() {
            return hasFailures() ? ScheduledTaskExecutionStatus.FAILED : ScheduledTaskExecutionStatus.SUCCESSFUL;
        }
    }
}
