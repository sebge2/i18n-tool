package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.workspace.dto.BundleFileDto;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

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
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces} restricted to the specified repository.
     */
    @GetMapping("/repository/{id}/workspace")
    @Operation(summary = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll(@PathVariable String id) {
        return workspaceManager.findAll(id)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }

    /**
     * Returns the {@link WorkspaceDto workspace} having the specified id.
     */
    @GetMapping(path = "/repository/workspace/{id}")
    @Operation(summary = "Returns the workspace having the specified id.")
    public Mono<WorkspaceDto> findById(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }

    /**
     * Returns {@link BundleFileDto bundle files} composing the specified workspace.
     */
    @GetMapping(path = "/repository/workspace/{id}/bundle-file")
    @Operation(summary = "Returns bundle files composing the specified workspace.")
    public Mono<List<BundleFileDto>> findWorkspaceBundleFiles(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .flatMapIterable(WorkspaceEntity::getFiles)
                .map(bundleFile -> BundleFileDto.builder(bundleFile).build())
                .collectList();
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
    @PostMapping(path = "/repository/{repositoryId}/workspace/do", params = "action=SYNCHRONIZE")
    @Operation(
            operationId = "synchronize",
            summary = "Executes an action on workspaces of a particular repository.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"SYNCHRONIZE"}))
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<WorkspaceDto> synchronizeRepository(@PathVariable String repositoryId) {
        return workspaceManager.synchronize(repositoryId)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

    /**
     * Initializes the {@link WorkspaceDto workspace} having the specified id and returns it.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=INITIALIZE")
    @Operation(
            operationId = "initialize",
            summary = "Initializes the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"INITIALIZE", "PUBLISH"}))
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> initialize(@PathVariable String id) {
        return workspaceManager.initialize(id)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }

    /**
     * Publishes the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=PUBLISH")
    @Operation(
            operationId = "publish",
            summary = "Publishes all modifications made on the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"INITIALIZE", "PUBLISH"}))
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> publish(@PathVariable String id,
                                      @Parameter(description = "Message required when publishing, it describe the changes.") @RequestParam(name = "message") String message) {

        if (StringUtils.isEmpty(message)) {
            throw BadRequestException.missingReviewMessage();
        }

        return workspaceManager.publish(id, message)
                .map(workspace -> WorkspaceDto.builder(workspace).build());
    }
}
