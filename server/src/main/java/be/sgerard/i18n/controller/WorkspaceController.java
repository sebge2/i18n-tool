package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.WorkspaceDto;
import be.sgerard.i18n.service.i18n.TranslationManager;
import be.sgerard.i18n.service.i18n.WorkspaceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @GetMapping("/workspace")
    @ApiOperation(value = "Returns registered workspaces.")
    public Flux<WorkspaceDto> findAll(@RequestParam(required = false, name = "repository") String repositoryId) {
        if (StringUtils.isEmpty(repositoryId)) {
            return workspaceManager.findAll()
                    .map(entity -> WorkspaceDto.builder(entity).build());
        } else {
            return workspaceManager.findAll(repositoryId)
                    .map(entity -> WorkspaceDto.builder(entity).build());
        }
    }

    @GetMapping(path = "/workspace/{id}")
    @ApiOperation(value = "Returns the workspace having the specified id.")
    public Mono<WorkspaceDto> getWorkspace(@PathVariable String id) {
        return workspaceManager.findByIdOrDie(id)
                .map(entity -> WorkspaceDto.builder(entity).build());
    }

//
//
//    @PutMapping(path = "/workspace")
//    @ApiOperation(value = "Executes an action on workspaces.")
//    @PreAuthorize("hasRole('ADMIN') and hasRole('MEMBER_OF_REPOSITORY')")
//    @SuppressWarnings("SwitchStatementWithTooFewBranches")
//    public void executeWorkspacesAction(@ApiParam("The action to execute.") @RequestParam(name = "do") WorkspaceListAction doAction) throws LockTimeoutException, RepositoryException {
//        // TODO
////        switch (doAction) {
////            case FIND:
////                workspaceManager.initialize();
////                break;
////        }
//    }
//
//    @DeleteMapping(path = "/workspace/{id}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    @ApiOperation(value = "Deletes the workspace having the specified id.")
//    @PreAuthorize("hasRole('ADMIN') and hasRole('MEMBER_OF_REPOSITORY')")
//    public void deleteWorkspace(@PathVariable String id) throws LockTimeoutException, RepositoryException {
//        workspaceManager.delete(id);
//    }
//
//
//
//
//    @PutMapping(path = "/workspace/{id}")
//    @ApiOperation(value = "Executes an action on the specified workspace.")
//    @PreAuthorize("hasRole('ADMIN') and hasRole('MEMBER_OF_REPOSITORY')")
//    public WorkspaceDto executeWorkspaceAction(@PathVariable String id,
//                                               @ApiParam("The action to execute.") @RequestParam(name = "do") WorkspaceAction doAction,
//                                               @ApiParam("Specify the message to use for the review.")
//                                               @RequestParam(name = "message", required = false) String message) throws LockTimeoutException, RepositoryException {
////        switch (doAction) {
////            case INITIALIZE:
////                return WorkspaceDto.builder(workspaceManager.initialize(id)).build();
////            case START_REVIEW:
////                if (StringUtils.isEmpty(message)) {
////                    throw new BadRequestException(
////                            "There is no message specify. A message is needed when starting a review.",
////                            "BadRequestException.start-review-no-message.message"
////                    );
////                }
////
////                return WorkspaceDto.builder(workspaceManager.publish(id, message)).build();
////            default:
//                throw BadRequestException.actionNotSupportedException(doAction.toString());
////        }
//    }
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
//
//    public enum WorkspaceListAction {
//
//        FIND
//    }
//
//    public enum WorkspaceAction {
//
//        INITIALIZE,
//
//        START_REVIEW
//    }
}
