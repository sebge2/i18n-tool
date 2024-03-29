package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.persistence.BundleFileEntity;
import be.sgerard.i18n.model.workspace.dto.BundleFileDto;
import be.sgerard.i18n.model.workspace.dto.WorkspaceDto;
import be.sgerard.i18n.model.workspace.dto.WorkspacesPublishRequestDto;
import be.sgerard.i18n.model.workspace.persistence.WorkspaceEntity;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.workspace.WorkspaceDtoEnricher;
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

import java.util.Comparator;
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
    private final WorkspaceDtoEnricher dtoEnricher;

    public WorkspaceController(WorkspaceManager workspaceManager, WorkspaceDtoEnricher dtoEnricher) {
        this.workspaceManager = workspaceManager;
        this.dtoEnricher = dtoEnricher;
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces}.
     */
    @GetMapping("/repository/workspace")
    @Operation(operationId = "findAll", summary = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll() {
        return workspaceManager.findAll()
                .flatMapSequential(dtoEnricher::mapAndEnrich);
    }

    /**
     * Finds all the {@link WorkspaceDto workspaces} restricted to the specified repository.
     */
    @GetMapping("/repository/{id}/workspace")
    @Operation(operationId = "findAllByRepository", summary = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll(@PathVariable String id) {
        return workspaceManager.findAll(id)
                .flatMapSequential(dtoEnricher::mapAndEnrich);
    }

    /**
     * Returns the {@link WorkspaceDto workspace} having the specified id.
     */
    @GetMapping(path = "/repository/workspace/{id}")
    @Operation(operationId = "findById", summary = "Returns the workspace having the specified id.")
    public Mono<WorkspaceDto> findById(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .flatMap(dtoEnricher::mapAndEnrich);
    }

    /**
     * Returns {@link BundleFileDto bundle files} composing the specified workspace.
     */
    @GetMapping(path = "/repository/workspace/{id}/bundle-file")
    @Operation(operationId = "findWorkspaceBundleFiles", summary = "Returns bundle files composing the specified workspace.")
    public Mono<List<BundleFileDto>> findWorkspaceBundleFiles(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .flatMapIterable(WorkspaceEntity::getFiles)
                .sort(Comparator.comparing(BundleFileEntity::getName))
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
            operationId = "synchronizeWorkspaces",
            summary = "Executes an action on workspaces of a particular repository.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"SYNCHRONIZE"}))
    )
    public Flux<WorkspaceDto> synchronizeRepository(@PathVariable String repositoryId) {
        return workspaceManager.synchronizeAll(repositoryId)
                .flatMapSequential(dtoEnricher::mapAndEnrich);
    }

    /**
     * Initializes the {@link WorkspaceDto workspace} having the specified id and returns it.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=INITIALIZE")
    @Operation(
            operationId = "initializeWorkspace",
            hidden = true,
            summary = "Initializes the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"INITIALIZE", "PUBLISH", "SYNCHRONIZE"}))
    )
    public Mono<WorkspaceDto> initialize(@PathVariable String id) {
        return workspaceManager.initialize(id)
                .flatMap(dtoEnricher::mapAndEnrich);
    }

    /**
     * Synchronizes the {@link WorkspaceDto workspace} having the specified id and returns it.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=SYNCHRONIZE")
    @Operation(
            operationId = "synchronizeWorkspace",
            hidden = true,
            summary = "Synchronizes the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"INITIALIZE", "PUBLISH", "SYNCHRONIZE"}))
    )
    public Mono<WorkspaceDto> synchronize(@PathVariable String id) {
        return workspaceManager.synchronize(id)
                .flatMap(dtoEnricher::mapAndEnrich);
    }

    /**
     * Publishes the modifications made on the specified workspace. Based on the type of repository, a review may start afterwards.
     * If it's not the case, a new fresh workspace will be created and returned.
     */
    @PostMapping(path = "/repository/workspace/{id}/do", params = "action=PUBLISH")
    @Operation(
            operationId = "publishWorkspace",
            summary = "Publishes all modifications made on the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"INITIALIZE", "PUBLISH", "SYNCHRONIZE"}))
    )
    public Mono<WorkspaceDto> publish(@PathVariable String id,
                                      @Parameter(description = "Message required when publishing, it describe the changes.") @RequestParam(name = "message") String message) {

        if (StringUtils.isEmpty(message)) {
            throw BadRequestException.missingReviewMessage();
        }

        return workspaceManager
                .publish(WorkspacesPublishRequestDto.builder().workspace(id).message(message).build())
                .next()
                .flatMap(dtoEnricher::mapAndEnrich);
    }

    /**
     * Publishes a list of workspaces.
     */
    @PostMapping(path = "/repository/workspace/do", params = "action=PUBLISH")
    @Operation(
            operationId = "publishWorkspaces",
            summary = "Initializes the specified workspace.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"PUBLISH"}))
    )
    public Flux<WorkspaceDto> publish(@RequestBody WorkspacesPublishRequestDto request) {
        if (StringUtils.isEmpty(request.getMessage())) {
            throw BadRequestException.missingReviewMessage();
        }

        return workspaceManager
                .publish(request)
                .flatMap(dtoEnricher::mapAndEnrich);
    }
}
