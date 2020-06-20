package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.dto.RepositorySummaryDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.repository.RepositoryDtoMapper;
import be.sgerard.i18n.service.repository.RepositoryManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.reactivestreams.Publisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link RestController Controller} handling {@link RepositoryDto repositories}.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller handling repositories.")
public class RepositoryController {

    private final RepositoryManager repositoryManager;
    private final RepositoryDtoMapper dtoMapper;

    public RepositoryController(RepositoryManager repositoryManager, RepositoryDtoMapper dtoMapper) {
        this.repositoryManager = repositoryManager;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Finds all the {@link RepositorySummaryDto repositories}.
     */
    @GetMapping(path = "/repository")
    @ApiOperation(value = "Finds all repositories.")
    @PreAuthorize("hasRole('ADMIN')")
    public Flux<RepositorySummaryDto> findAll() {
        return repositoryManager.findAll()
                .map(entity -> RepositorySummaryDto.builder(entity).build());
    }

    /**
     * Returns the {@link RepositoryDto repository} having the specified id.
     */
    @GetMapping(path = "/repository/{id}")
    @ApiOperation(value = "Finds the repository having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<RepositoryDto> findById(@PathVariable String id) {
        return repositoryManager.findByIdOrDie(id)
                .map(dtoMapper::mapToDto);
    }

    /**
     * Creates a new {@link RepositoryDto repository} based on the {@link RepositoryCreationDto DTO}.
     */
    @PostMapping(path = "/repository")
    @ApiOperation(value = "Creates a new repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RepositoryDto> create(@RequestBody RepositoryCreationDto creationDto) {
        return repositoryManager.create(creationDto)
                .map(dtoMapper::mapToDto);
    }

    /**
     * Updates the repository as described by the specified {@link RepositoryPatchDto DTO}.
     */
    @PatchMapping(path = "/repository/{id}")
    @ApiOperation(value = "Updates an existing repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public Mono<RepositoryDto> update(@PathVariable String id,
                                      @RequestBody RepositoryPatchDto patchDto) {
        if (!Objects.equals(id, patchDto.getId())) {
            return Mono.error(BadRequestException.idRequestNotMatchIdBodyException(id, patchDto.getId()));
        }

        return repositoryManager.update(patchDto)
                .map(dtoMapper::mapToDto);
    }

    @PostMapping(path = "/repository/{id}/do")
    @ApiOperation(value = "Executes an action on a repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public Mono<RepositoryDto> executeRepositoryAction(@PathVariable String id,
                                                       @RequestParam RepositoryAction action) {
        switch (action) {
            case INITIALIZE:
                return repositoryManager.initialize(id)
                        .map(dtoMapper::mapToDto);
            default:
                return Mono.error(BadRequestException.actionNotSupportedException(action.toString()));
        }
    }

    /**
     * Removes the {@link RepositoryDto repository} having the specified id.
     */
    @DeleteMapping(path = "/repository/{id}")
    @ApiOperation(value = "Delete a repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<RepositoryDto> delete(@PathVariable String id) {
        return repositoryManager.delete(id)
                .map(dtoMapper::mapToDto);
    }

    /**
     * All the possible actions that can be performed over a repository.
     */
    public enum RepositoryAction {

        /**
         * @see RepositoryManager#initialize(String)
         */
        INITIALIZE
    }

}
