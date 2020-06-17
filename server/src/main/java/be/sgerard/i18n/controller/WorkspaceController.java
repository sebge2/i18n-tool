package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.workspace.WorkspaceDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.workspace.WorkspaceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(value = "Controller handling workspaces.")
public class WorkspaceController {

    private final WorkspaceManager workspaceManager;
    private final TranslationManager translationManager;

    public WorkspaceController(WorkspaceManager workspaceManager, TranslationManager translationManager) {
        this.workspaceManager = workspaceManager;
        this.translationManager = translationManager;
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
     * Executes an action on workspaces associated to the specified repository.
     */
    @PostMapping(path = "/repository/{repositoryId}/workspace/do")
    @ApiOperation(value = "Executes an action on workspaces of a particular repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public Flux<WorkspaceDto> executeWorkspacesAction(@PathVariable String repositoryId,
                                                      @ApiParam("The action to execute.") @RequestParam(name = "action") RepositionWorkspacesAction action) {
        switch (action) {
            case SYNCHRONIZE:
                return workspaceManager.synchronize(repositoryId)
                        .map(entity -> WorkspaceDto.builder(entity).build());
            default:
                return Flux.error(BadRequestException.actionNotSupportedException(action.name()));
        }
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
    @PostMapping(path = "/repository/workspace/{id}/do")
    @ApiOperation(value = "Executes an action on the specified workspace.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<WorkspaceDto> executeWorkspaceAction(@PathVariable String id,
                                                     @ApiParam("The action to execute.") @RequestParam(name = "action") WorkspaceAction action,
                                                     @ApiParam("Specify the message to use for publishing.")
                                                     @RequestParam(name = "message", required = false) String message) {
        switch (action) {
            case INITIALIZE:
                return workspaceManager.initialize(id)
                        .map(workspace -> WorkspaceDto.builder(workspace).build());

            case PUBLISH:
                if (StringUtils.isEmpty(message)) {
                    return Mono.error(BadRequestException.missingReviewMessage());
                }

                return workspaceManager.publish(id, message)
                        .map(workspace -> WorkspaceDto.builder(workspace).build());
            default:
                return Mono.error(BadRequestException.actionNotSupportedException(action.toString()));
        }
    }

    //
//    @GetMapping(path = "/workspace/{id}/translation")
//    @ApiOperation(value = "Returns translations of the workspace having the specified id.")
//    public BundleKeysPageDto getWorkspaceTranslations(@PathVariable String id,
//                                                      @RequestParam(name = "locales", required = false, defaultValue = "") List<Locale> locales,
//                                                      @RequestParam(name = "criterion", required = false) TranslationSearchCriterion criterion,
//                                                      @RequestParam(name = "lastKey", required = false) String lastKey,
//                                                      @RequestParam(name = "maxKeys", required = false) Integer maxKeys) {
//        return translationManager.getTranslations(
//                BundleKeysPageRequestDto.builder(id)
//                        .locales(locales)
//                        .lastKey(lastKey)
//                        .maxKeys(maxKeys)
//                        .criterion(criterion)
//                        .build()
//        );
//    }
//
//    @RequestMapping(path = "/workspace/{id}/translation", method = RequestMethod.PATCH)
//    @ApiOperation(value = "Updates translations of the workspace having the specified id.")
//    public void updateTranslations(@PathVariable String id,
//                                   @RequestBody Map<String, String> translations) {
//        workspaceManager.updateTranslations(id, translations);
//    }

    /**
     * All possible actions that can be performed on workspaces.
     */
    public enum RepositionWorkspacesAction {

        SYNCHRONIZE
    }

    /**
     * All possible actions that can be performed on a single workspace.
     */
    public enum WorkspaceAction {

        INITIALIZE,

        PUBLISH
    }
}
