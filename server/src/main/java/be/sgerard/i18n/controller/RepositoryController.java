package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.repository.dto.RepositoryCreationDto;
import be.sgerard.i18n.model.repository.dto.RepositoryDto;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.repository.RepositoryDtoMapper;
import be.sgerard.i18n.service.repository.RepositoryManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Repository", description = "Controller handling repositories.")
public class RepositoryController {

    private final RepositoryManager repositoryManager;
    private final RepositoryDtoMapper dtoMapper;

    public RepositoryController(RepositoryManager repositoryManager, RepositoryDtoMapper dtoMapper) {
        this.repositoryManager = repositoryManager;
        this.dtoMapper = dtoMapper;
    }

    /**
     * Finds all the {@link RepositoryDto repositories}.
     */
    @GetMapping(path = "/repository")
    @Operation(operationId = "findAll", summary = "Finds all repositories.")
    public Flux<RepositoryDto> findAll() {
        return repositoryManager.findAll()
                .map(dtoMapper::mapToDto);
    }

    /**
     * Returns the {@link RepositoryDto repository} having the specified id.
     */
    @GetMapping(path = "/repository/{id}")
    @Operation(operationId = "findById", summary = "Finds the repository having the specified id.")
    public Mono<RepositoryDto> findById(@PathVariable String id) {
        return repositoryManager.findByIdOrDie(id)
                .map(dtoMapper::mapToDto);
    }

    /**
     * Creates a new {@link RepositoryDto repository} based on the {@link RepositoryCreationDto DTO}.
     */
    @PostMapping(path = "/repository")
    @Operation(operationId = "create", summary = "Creates a new repository.")
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
    @Operation(operationId = "update", summary = "Updates an existing repository.")
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

    @PostMapping(path = "/repository/{id}/do", params = "action=INITIALIZE")
    @Operation(
            summary = "Executes an action on a repository.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "INITIALIZE"))
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<RepositoryDto> initialize(@PathVariable String id) {
        return repositoryManager.initialize(id)
                .map(dtoMapper::mapToDto);
    }

    /**
     * Removes the {@link RepositoryDto repository} having the specified id.
     */
    @DeleteMapping(path = "/repository/{id}")
    @Operation(operationId = "delete", summary = "Delete a repository.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<RepositoryDto> delete(@PathVariable String id) {
        return repositoryManager.delete(id)
                .map(dtoMapper::mapToDto);
    }
}
