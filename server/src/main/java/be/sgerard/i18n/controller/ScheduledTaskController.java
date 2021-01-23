package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskDefinitionPatchDto;
import be.sgerard.i18n.model.scheduler.dto.ScheduledTaskExecutionDto;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionStatus;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.scheduler.ScheduledTaskExecutionManager;
import be.sgerard.i18n.service.scheduler.ScheduledTaskManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.asList;

/**
 * {@link RestController Controller} handling {@link ScheduledTaskDefinitionDto scheduled task definitions}.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "Scheduled Task", description = "Controller handling scheduled tasks.")
public class ScheduledTaskController {

    private final ScheduledTaskManager definitionManager;
    private final ScheduledTaskExecutionManager executionManager;

    public ScheduledTaskController(ScheduledTaskManager definitionManager,
                                   ScheduledTaskExecutionManager executionManager) {
        this.definitionManager = definitionManager;
        this.executionManager = executionManager;
    }

    /**
     * Finds all {@link ScheduledTaskDefinitionDto scheduled task definitions}.
     */
    @GetMapping("/scheduled-task/definition")
    @Operation(operationId = "findDefinitions", summary = "Returns scheduled task definitions.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Flux<ScheduledTaskDefinitionDto> findDefinitions(@Parameter(description = "Are tasks enabled?") @RequestParam(name = "enabled", required = false) Boolean enabled) {
        final ScheduledTaskDefinitionSearchRequest request = ScheduledTaskDefinitionSearchRequest.builder()
                .enabled(enabled)
                .build();

        return definitionManager.find(request)
                .map(definition -> ScheduledTaskDefinitionDto.builder(definition).build());
    }

    /**
     * Finds the {@link ScheduledTaskDefinitionDto scheduled task definition} having the specified.
     */
    @GetMapping("/scheduled-task/{id}/definition")
    @Operation(operationId = "findDefinitionById", summary = "Returns the scheduled task definition having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<ScheduledTaskDefinitionDto> findDefinitionById(@PathVariable String id) {
        return definitionManager.findByIdOrDie(id)
                .map(definition -> ScheduledTaskDefinitionDto.builder(definition).build());
    }

    /**
     * Enables the task having the specified id.
     */
    @PostMapping(path = "/scheduled-task/{id}/definition", params = "action=ENABLE")
    @Operation(
            operationId = "updateStatus",
            summary = "Enables/Disables the task having the specified id.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"ENABLE", "DISABLE"}))
    )
    @ResponseBody
    public Mono<ScheduledTaskDefinitionDto> enable(@PathVariable String id) {
        return definitionManager
                .enable(id)
                .map(definition -> ScheduledTaskDefinitionDto.builder(definition).build());
    }

    /**
     * Disables the task having the specified id.
     */
    @PostMapping(path = "/scheduled-task/{id}/definition", params = "action=DISABLE")
    @Operation(
            operationId = "updateStatus",
            hidden = true,
            summary = "Enables/Disables the task having the specified id.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"SCHEDULE", "DISABLE"}))
    )
    @ResponseBody
    public Mono<ScheduledTaskDefinitionDto> disable(@PathVariable String id) {
        return definitionManager
                .disable(id)
                .map(definition -> ScheduledTaskDefinitionDto.builder(definition).build());
    }

    /**
     * Updates the scheduled task definition as described by the specified {@link ScheduledTaskDefinitionPatchDto DTO}.
     */
    @PatchMapping(path = "/scheduled-task/{id}/definition")
    @Operation(operationId = "updateDefinition", summary = "Updates a scheduled task definition.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<ScheduledTaskDefinitionDto> updateDefinition(@PathVariable String id,
                                                             @RequestBody ScheduledTaskDefinitionPatchDto patchDto) {
        if (!Objects.equals(id, patchDto.getId())) {
            return Mono.error(BadRequestException.idRequestNotMatchIdBodyException(id, patchDto.getId()));
        }

        return definitionManager
                .update(patchDto)
                .map(definition -> ScheduledTaskDefinitionDto.builder(definition).build());
    }

    /**
     * Finds all {@link ScheduledTaskExecutionDto scheduled task executions}.
     */
    @GetMapping("/scheduled-task/execution")
    @Operation(operationId = "findExecutions", summary = "Returns scheduled task executions.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Flux<ScheduledTaskExecutionDto> findExecutions(
            @Parameter(description = "From the oldest to the newest execution.") @RequestParam(name = "ascending", defaultValue = "false", required = false) boolean ascending,
            @Parameter(description = "Id of the associated task definition") @RequestParam(name = "taskDefinitionId", required = false) String taskDefinitionId,
            @Parameter(description = "The maximum age (inclusive) of the execution in EPOCH time (seconds)") @RequestParam(name = "newerOrEqualThan", required = false) Long newerOrEqualThan,
            @Parameter(description = "The minimum age (inclusive) of the execution in EPOCH time (seconds)") @RequestParam(name = "olderOrEqualThan", required = false) Long olderOrEqualThan,
            @Parameter(description = "Status to look for") @RequestParam(name = "status", required = false) List<ScheduledTaskExecutionStatus> status,
            @Parameter(description = "Maximum number of results") @RequestParam(name = "limit", required = false, defaultValue = ScheduledTaskExecutionSearchRequest.DEFAULT_LIMIT + "") int limit
    ) {
        final ScheduledTaskExecutionSearchRequest searchRequest = ScheduledTaskExecutionSearchRequest.builder()
                .ascending(ascending)
                .executedAfterOrEqualThan(Optional.ofNullable(newerOrEqualThan).map(Instant::ofEpochSecond).orElse(null))
                .executedBeforeOrEqualThan(Optional.ofNullable(olderOrEqualThan).map(Instant::ofEpochSecond).orElse(null))
                .taskDefinitionId(taskDefinitionId)
                .statuses(Optional.ofNullable(status).orElse(asList(ScheduledTaskExecutionStatus.values())))
                .limit(limit)
                .build();

        return executionManager
                .find(searchRequest)
                .map(execution -> ScheduledTaskExecutionDto.builder(execution).build());
    }
}
