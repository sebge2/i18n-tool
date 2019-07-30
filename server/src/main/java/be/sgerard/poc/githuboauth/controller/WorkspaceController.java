package be.sgerard.poc.githuboauth.controller;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeysPageDto;
import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeysPageRequestDto;
import be.sgerard.poc.githuboauth.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.poc.githuboauth.model.i18n.dto.WorkspaceDto;
import be.sgerard.poc.githuboauth.service.LockTimeoutException;
import be.sgerard.poc.githuboauth.service.ResourceNotFoundException;
import be.sgerard.poc.githuboauth.service.git.RepositoryException;
import be.sgerard.poc.githuboauth.service.i18n.TranslationManager;
import be.sgerard.poc.githuboauth.service.i18n.WorkspaceManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
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

    @GetMapping("/workspace")
    @ApiOperation(value = "Returns registered workspaces.")
    public List<WorkspaceDto> getWorkspaces() {
        return workspaceManager.getWorkspaces()
            .stream()
            .map(entity -> WorkspaceDto.builder(entity).build())
            .collect(toList());
    }

    @PutMapping(path = "/workspace")
    @ApiOperation(value = "Executes an action on workspaces.")
    public void executeWorkspacesAction(@ApiParam("The action to execute.") @RequestParam(name = "do") WorkspaceListAction doAction) throws LockTimeoutException, RepositoryException {
        switch (doAction) {
            case FIND:
                workspaceManager.findWorkspaces();
                break;
        }
    }

    @GetMapping(path = "/workspace/{id}")
    @ApiOperation(value = "Returns the workspace having the specified id.")
    public WorkspaceDto getWorkspace(@PathVariable String id) {
        return workspaceManager.getWorkspace(id)
            .map(entity -> WorkspaceDto.builder(entity).build())
            .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    @DeleteMapping(path = "/workspace/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes the workspace having the specified id.")
    public void deleteWorkspace(@PathVariable String id) throws LockTimeoutException, RepositoryException {
        workspaceManager.deleteWorkspace(id);
    }

    @PutMapping(path = "/workspace/{id}")
    @ApiOperation(value = "Executes an action on the specified workspace.")
    public WorkspaceDto executeWorkspaceAction(@PathVariable String id,
                                               @ApiParam("The action to execute.") @RequestParam(name = "do") WorkspaceAction doAction,
                                               @ApiParam("Specify the message to use for the review.")
                                               @RequestParam(name = "message", required = false) String message) throws LockTimeoutException, RepositoryException {
        switch (doAction) {
            case INITIALIZE:
                return WorkspaceDto.builder(workspaceManager.initialize(id)).build();
            case START_REVIEW:
                if (StringUtils.isEmpty(message)) {
                    throw new IllegalArgumentException("There is no message specify. A message is needed when starting a review.");
                }

                return WorkspaceDto.builder(workspaceManager.startReviewing(id, message)).build();
            default:
                throw new IllegalArgumentException("Action " + doAction + " is not supported.");
        }
    }

    @GetMapping(path = "/workspace/{id}/translation")
    @ApiOperation(value = "Returns translations of the workspace having the specified id.")
    public BundleKeysPageDto getWorkspaceTranslations(@PathVariable String id,
                                                      @RequestParam(name = "locales", required = false, defaultValue = "") List<Locale> locales,
                                                      @RequestParam(name = "criterion", required = false) TranslationSearchCriterion criterion,
                                                      @RequestParam(name = "lastKey", required = false) String lastKey,
                                                      @RequestParam(name = "maxKeys", required = false) Integer maxKeys) {
        return translationManager.getTranslations(
            BundleKeysPageRequestDto.builder(id)
                .locales(locales)
                .lastKey(lastKey)
                .maxKeys(maxKeys)
                .criterion(criterion)
                .build()
        );
    }

    @RequestMapping(path = "/workspace/{id}/translation", method = RequestMethod.PATCH)
    @ApiOperation(value = "Updates translations of the workspace having the specified id.")
    public void updateTranslations(@PathVariable String id,
                                   @RequestBody Map<String, String> translations) {
        workspaceManager.updateTranslations(id, translations);
    }

    @GetMapping(path = "/workspace/registered-locale")
    @ApiOperation(value = "Returns all locales that have been used so far.")
    public Collection<String> getRegisteredLocales() {
        return translationManager.getRegisteredLocales().stream().map(Locale::toString).collect(toSet());
    }

    public enum WorkspaceListAction {

        FIND
    }

    public enum WorkspaceAction {

        INITIALIZE,

        START_REVIEW
    }
}
