package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * {@link RestController Controller} handling {@link WorkspaceDto workspaces}.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Tag(name = "Workspace", description = "Controller handling workspaces.")
public class WorkspaceController {

    private final WorkspaceManager workspaceManager;

    public WorkspaceController(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces}.
     */
    @GetMapping("/repository/workspace")
    @Operation(summary = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll() {
        return workspaceManager.findAll()
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces} restricted to the specified repository.
     */
    @GetMapping("/repository/{id}/workspace")
    @Operation(summary = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll(@PathVariable String id) {
        return workspaceManager.findAll(id)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Returns the {@link WorkspaceDto workspace} having the specified id.
     */
    @GetMapping(path = "/repository/workspace/{id}")
    @Operation(summary = "Returns the workspace having the specified id.")
    public Mono<WorkspaceDto> findById(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Synchronizes the current workspaces with the specified repository: missing ones are created, workspaces
     * that are no more relevant (branch does not exist anymore) are deleted.
     */
    @PostMapping(path = "/repository/{repositoryId}/workspace/do", params = "action=SYNCHRONIZE")
    @Operation(summary = "Executes an action on workspaces of a particular repository.",
            parameters = {@Parameter(name = "action", examples = {
                    @ExampleObject(value = "SYNCHRONIZE")
            })}
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<WorkspaceDto> synchronizeWorkspaces(@PathVariable String repositoryId) {
        return workspaceManager.synchronize(repositoryId)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Removes the {@link WorkspaceDto workspace} having the specified id.
     */
    @DeleteMapping(path = "/repository/workspace/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletes the workspace having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteWorkspace(@PathVariable String id) {
        return workspaceManager.delete(id).then();
    }

    /**
     * Executes an action on workspaces associated to the specified repository.
     */
    @PostMapping(path = "/repository/{repositoryId}/workspace/do")
    @Operation(summary = "Executes an action on workspaces of a particular repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public Flux<WorkspaceDto> executeWorkspacesAction(@PathVariable String repositoryId,
                                                      @Parameter(description = "The action to execute.") @RequestParam(name = "action") WorkspacesAction action) {
        switch (action) {
            case SYNCHRONIZE:
                return workspaceManager.synchronize(repositoryId)
                        .map(entity -> WorkspaceDto.builder(entity).build());
            default:
                throw BadRequestException.actionNotSupportedException(action.name());
        }
    }

    /**
     * Publishes all the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    @PostMapping(path = "/repository/workspace/{id}/do")
    @Operation(summary = "Executes an action on the specified workspace.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> executeWorkspaceAction(@PathVariable String id,
                                                     @Parameter(description = "The action to execute.") @RequestParam(name = "action") WorkspaceAction action,
                                                     @Parameter(description = "Message required when publishing, it describe the changes.") @RequestParam(name = "message", required = false) String message) {
        switch (action) {
            case INITIALIZE:
                return workspaceManager.initialize(id)
                        .map(workspace -> WorkspaceDto.builder(workspace).build());
            case PUBLISH:
                if (StringUtils.isEmpty(message)) {
                    throw BadRequestException.missingReviewMessage();
                }

                return workspaceManager.publish(id, message)
                        .map(workspace -> WorkspaceDto.builder(workspace).build());
            default:
                throw BadRequestException.actionNotSupportedException(action.toString());
        }
    }

    /**
     * Action possible on a group of workspaces.
     */
    public enum WorkspacesAction {

        SYNCHRONIZE
    }

    /**
     * Action possible on a workspace.
     */
    public enum WorkspaceAction {

        INITIALIZE,

        PUBLISH
    }
}
