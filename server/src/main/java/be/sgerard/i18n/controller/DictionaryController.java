package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryCreationDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryDto;
import be.sgerard.i18n.model.dictionary.dto.DictionaryEntryPatchDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.dictionary.DictionaryExporter;
import be.sgerard.i18n.service.dictionary.DictionaryImporter;
import be.sgerard.i18n.service.dictionary.DictionaryManager;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.Set;

import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimWhitespace;

/**
 * Controller handling the application's dictionary.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api/dictionary")
@Tag(name = "Dictionary", description = "Controller handling the application's dictionary.")
public class DictionaryController {

    private final DictionaryManager manager;
    private final DictionaryImporter importer;
    private final DictionaryExporter exporter;

    public DictionaryController(DictionaryManager manager,
                                DictionaryImporter importer,
                                DictionaryExporter exporter) {
        this.manager = manager;
        this.importer = importer;
        this.exporter = exporter;
    }

    /**
     * Retrieves dictionary entries.
     */
    @GetMapping("")
    @Operation(operationId = "search", summary = "Retrieves dictionary entries.")
    public Flux<DictionaryEntryDto> search(
            @Parameter(description = "Text to search for") @RequestParam(name = "text", required = false) String text,
            @Parameter(description = "ID of the locale in which the specified text is written") @RequestParam(name = "textLocaleId", required = false) String textLocaleId,
            @Parameter(description = "ID of the locale of translations to sort on") @RequestParam(name = "sortLocaleId", required = false) String sortLocaleId,
            @Parameter(description = "Flag indicating whether the sorting is ascending (or descending).") @RequestParam(name = "sortAscending", required = false, defaultValue = "true") boolean sortAscending,
            @Parameter(description = "ID of the locales") @RequestParam(name = "localeId", required = false, defaultValue = "") Set<String> localeIds
    ) {
        final String updatedText = trimWhitespace(text);
        final String updatedLocaleId = trimWhitespace(textLocaleId);
        final String updatedSortLocaleId = trimWhitespace(sortLocaleId);

        if (isEmpty(updatedText) != isEmpty(updatedLocaleId)) {
            throw BadRequestException.parametersMustBeSpecifiedTogether("text", "textLocaleId");
        }

        return manager
                .find(
                        DictionaryEntrySearchRequest.builder()
                                .translation(
                                        (!isEmpty(updatedText) && !isEmpty(updatedLocaleId))
                                                ? new DictionaryEntrySearchRequest.TranslationRestriction(updatedText, updatedLocaleId)
                                                : null
                                )
                                .sort(
                                        !isEmpty(updatedSortLocaleId)
                                                ? new DictionaryEntrySearchRequest.Sort(sortAscending, updatedSortLocaleId)
                                                : null
                                )
                                .build()
                )
                .map(entry -> DictionaryEntryDto.builder(entry, localeIds).build());
    }

    /**
     * Returns the {@link DictionaryEntryDto entry} having the specified id.
     */
    @GetMapping(path = "/{id}")
    @Operation(operationId = "findById", summary = "Finds the dictionary entry having the specified id.")
    public Mono<DictionaryEntryDto> findById(@PathVariable String id) {
        return manager
                .findByIdOrDie(id)
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Creates a new {@link DictionaryEntryDto entry} based on the {@link DictionaryEntryCreationDto DTO}.
     */
    @PostMapping(path = "")
    @Operation(operationId = "create", summary = "Creates a new entry.")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<DictionaryEntryDto> create(@RequestBody DictionaryEntryCreationDto creationDto) {
        return manager
                .create(creationDto)
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Exports the dictionary to a CSV file.
     */
    @GetMapping(path = "/do", produces = "text/csv")
    @Operation(
            operationId = "exportDictionary",
            summary = "Exports the dictionary to a CSV file.",
            responses = @ApiResponse(content = @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))),
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "export"))
    )
    @ResponseBody
    public Mono<ResponseEntity<Flux<String>>> exportDictionary() {
        return exporter
                .exportToCsv()
                .map(pair ->
                        ResponseEntity.ok()
                                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + pair.getFirst())
                                .body(pair.getSecond())
                );
    }

    /**
     * Imports the dictionary from the CSV file.
     */
    @PutMapping(path = "/do", consumes = {"text/csv"}, params = "action=import")
    @Operation(
            operationId = "importDictionary",
            summary = "Imports the dictionary from the CSV file.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(content = {
                    @Content(mediaType = "text/csv", schema = @Schema(type = "string", format = "binary"))
            }),
            parameters = @Parameter(name = "action", in = ParameterIn.QUERY, schema = @Schema(allowableValues = "import"))
    )
    public Flux<DictionaryEntryDto> importDictionary(ServerHttpRequest request,
                                                     @Parameter(description = "Persist the import, or just parse it") @RequestParam(name = "persist", required = false, defaultValue = "true") boolean persist) {
        return DataBufferUtils.join(request.getBody())
                .map(DataBuffer::asInputStream)
                .flatMapMany(stream -> importer.importFromCsv(stream, persist))
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Updates the entry as described by the specified {@link DictionaryEntryPatchDto DTO}.
     */
    @PatchMapping(path = "/{id}")
    @Operation(operationId = "updateEntry", summary = "Updates an existing entry.")
    @ResponseStatus(HttpStatus.OK)
    public Mono<DictionaryEntryDto> update(@PathVariable String id,
                                           @RequestBody DictionaryEntryPatchDto patch) {
        if (!Objects.equals(id, patch.getId())) {
            return Mono.error(BadRequestException.idRequestNotMatchIdBodyException(id, patch.getId()));
        }

        return manager
                .update(patch)
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Updates the specified {@link DictionaryEntryDto dictionary entries} and returns them.
     */
    @PatchMapping(path = "")
    @Operation(operationId = "updateEntries", summary = "Updates dictionary entries.")
    public Flux<DictionaryEntryDto> updateTranslations(@RequestBody(required = false) List<DictionaryEntryPatchDto> patches) {
        final List<DictionaryEntryPatchDto> mergedPatches = DictionaryEntryPatchDto.mergePatches(patches);

        return manager
                .update(mergedPatches)
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Deletes the {@link DictionaryEntryDto entry} having the specified id.
     */
    @DeleteMapping(path = "/{id}")
    @Operation(operationId = "delete", summary = "Delete an entry.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<DictionaryEntryDto> delete(@PathVariable String id) {
        return manager
                .delete(id)
                .map(entry -> DictionaryEntryDto.builder(entry).build());
    }

    /**
     * Deletes all {@link DictionaryEntryDto entries}.
     */
    @DeleteMapping(path = "")
    @Operation(operationId = "deleteAll", summary = "Delete all entries.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteAll() {
        return manager.deleteAll();
    }
}
