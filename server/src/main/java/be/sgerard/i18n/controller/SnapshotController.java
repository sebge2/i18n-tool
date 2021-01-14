package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.snapshot.dto.SnapshotCreationDto;
import be.sgerard.i18n.model.snapshot.dto.SnapshotDto;
import be.sgerard.i18n.service.snapshot.SnapshotManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * Controller managing snapshots.
 *
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping(path = "/api")
@Tag(name = "Snapshot", description = "Controller managing snapshots")
public class SnapshotController {

    private final SnapshotManager snapshotManager;

    public SnapshotController(SnapshotManager snapshotManager) {
        this.snapshotManager = snapshotManager;
    }

    /**
     * Lists all available {@link SnapshotDto snapshots}.
     */
    @GetMapping(path = "/snapshot")
    @Operation(operationId = "findAll", summary = "Lists all available snapshots")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Flux<SnapshotDto> findAll() {
        return snapshotManager
                .findAll()
                .map(snapshot -> SnapshotDto.builder(snapshot).build());
    }

    /**
     * Creates a new {@link SnapshotDto snapshot} based on the current tool state.
     */
    @PostMapping(path = "/snapshot")
    @Operation(operationId = "create", summary = "Creates a new snapshot based on the current tool state")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<SnapshotDto> create(@RequestBody SnapshotCreationDto creationDto) {
        return snapshotManager
                .create(creationDto)
                .map(snapshot -> SnapshotDto.builder(snapshot).build());
    }

    /**
     * Exports the content of the specified {@link SnapshotDto snapshot}.
     */
    @GetMapping(path = "/snapshot/{id}/file", produces = "application/zip")
    @Operation(
            operationId = "exportZip",
            summary = "Exports the content of the specified snapshot",
            responses = @ApiResponse(content = @Content(mediaType = "application/zip", schema = @Schema(type = "string", format = "binary")))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<ResponseEntity<Flux<DataBuffer>>> exportZip(@PathVariable String id) {
        return snapshotManager
                .exportZip(id)
                .map(pair ->
                        ResponseEntity.ok()
                                .header("Content-Type", "application/zip")
                                .header("Content-Disposition", "attachment; filename=" + pair.getFirst())
                                .body(pair.getSecond())
                );
    }

    /**
     * Imports the specified {@link SnapshotDto snapshot} file.
     */
    @PostMapping(path = "/snapshot/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(operationId = "importZip", summary = "Import the specified snapshot ZIP file (the form must be composed of a file and optionally of the ZIP's password)")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<SnapshotDto> importZip(@RequestPart(name = "encryptionPassword", required = false) @Schema(type = "string") FormFieldPart encryptionPasswordPart,
                                       @RequestPart(name = "file") @Schema(type = "string", format = "binary") FilePart filePart) {
        return snapshotManager.importZip(filePart::transferTo, filePart.filename(), Optional.ofNullable(encryptionPasswordPart).map(FormFieldPart::value).orElse(null))
                .map(snapshot -> SnapshotDto.builder(snapshot).build());
    }

    /**
     * Restores the specified snapshot, the tool state will be restored to that state.
     */
    @PostMapping(path = "/snapshot/{id}/do", params = "action=RESTORE")
    @Operation(
            operationId = "restore",
            summary = "Restores the specified snapshot, the tool state will be restored to that state.",
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = {"RESTORE"}))
    )
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<SnapshotDto> restore(@PathVariable String id) {
        return snapshotManager.restore(id)
                .map(snapshot -> SnapshotDto.builder(snapshot).build());
    }

    /**
     * Removes the specified {@link SnapshotDto snapshot}.
     */
    @DeleteMapping(path = "/snapshot/{id}")
    @Operation(operationId = "delete", summary = "Removes the specified snapshot")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return snapshotManager.delete(id);
    }
}
