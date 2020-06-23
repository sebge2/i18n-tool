package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Api(value = "Controller handling workspaces.")
public class WorkspaceController {

    private final WorkspaceManager workspaceManager;

    public WorkspaceController(WorkspaceManager workspaceManager) {
        this.workspaceManager = workspaceManager;
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces}.
     */
    @GetMapping("/repository/workspace")
    @ApiOperation(value = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll() {
        return workspaceManager.findAll()
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces} restricted to the specified repository.
     */
    @GetMapping("/repository/{id}/workspace")
    @ApiOperation(value = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll(@PathVariable String id) {
        return workspaceManager.findAll(id)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Returns the {@link WorkspaceDto workspace} having the specified id.
     */
    @GetMapping(path = "/repository/workspace/{id}")
    @ApiOperation(value = "Returns the workspace having the specified id.")
    public Mono<WorkspaceDto> findById(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Synchronizes the current workspaces with the specified repository: missing ones are created, workspaces
     * that are no more relevant (branch does not exist anymore) are deleted.
     */
    @PostMapping(path = "/repository/{repositoryId}/workspace/do", params = "action=SYNCHRONIZE")
    @ApiOperation(value = "Executes an action on workspaces of a particular repository.")
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
    @ApiOperation(value = "Deletes the workspace having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> deleteWorkspace(@PathVariable String id) {
        return workspaceManager.delete(id).then();
    }

    /**
     * Executes an action on the specified workspace.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=INITIALIZE")
    @ApiOperation(value = "Executes an action on the specified workspace.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> synchronizeWorkspace(@PathVariable String id) {
        return workspaceManager.initialize(id)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }

    /**
     * Publishes all the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=PUBLISH")
    @ApiOperation(value = "Executes an action on the specified workspace.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> publishWorkspace(@PathVariable String id,
                                               @ApiParam("Specify the message to use for publishing.")
                                               @RequestParam(name = "message") String message) {
        return workspaceManager.publish(id, message)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }
}
