package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.translator.dto.AzureTranslatorConfigDto;
import be.sgerard.i18n.model.translator.dto.ExternalTranslatorConfigDto;
import be.sgerard.i18n.model.translator.dto.GoogleTranslatorConfigDto;
import be.sgerard.i18n.model.translator.dto.ITranslateTranslatorConfigDto;
import be.sgerard.i18n.model.translator.persistence.ExternalTranslatorConfigEntity;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.translator.ExternalTranslatorConfigManager;
import be.sgerard.i18n.service.translator.dto.ExternalTranslatorConfigDtoMapper;
import be.sgerard.i18n.service.translator.template.ExternalTranslatorConfigProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link RestController Controller} handling {@link ExternalTranslatorConfigDto external translator config}.
 *
 * @author Sebastien Gerard
 */
@Controller
@RequestMapping(path = "/api")
@Tag(name = "ExternalTranslator", description = "Controller exposing external translators.")
public class ExternalTranslatorController {

    private final ExternalTranslatorConfigManager manager;
    private final ExternalTranslatorConfigProvider<Object> configProvider;
    private final ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> mapper;

    public ExternalTranslatorController(ExternalTranslatorConfigManager manager,
                                        ExternalTranslatorConfigProvider<Object> configProvider,
                                        ExternalTranslatorConfigDtoMapper<ExternalTranslatorConfigEntity, ExternalTranslatorConfigDto> mapper) {
        this.manager = manager;
        this.configProvider = configProvider;
        this.mapper = mapper;
    }

    /**
     * Finds all external translator configurations.
     */
    @GetMapping(path = "/external-translator")
    @Operation(operationId = "findAll", summary = "Find all external translator configurations.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Flux<ExternalTranslatorConfigDto> findAll() {
        return manager.findAll()
                .map(mapper::mapToDto);
    }

    /**
     * Returns the external translator configuration having the specified id.
     */
    @GetMapping(path = "/external-translator/{id}")
    @Operation(operationId = "findById", summary = "Returns the external translator configuration having the specified id.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> findById(@PathVariable String id) {
        return manager.findByIdOrDie(id)
                .map(mapper::mapToDto);
    }

    /**
     * Creates a configuration translator.
     */
    @PostMapping(path = "/external-translator")
    @Operation(operationId = "createTranslatorConfig", summary = "Creates a translator configuration.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> createTranslator(@RequestBody ExternalTranslatorConfigDto config) {
        return manager
                .create(config)
                .map(mapper::mapToDto);
    }

    /**
     * Creates a configuration for Google translator.
     */
    @PostMapping(path = "/external-translator/google")
    @Operation(operationId = "createGoogleTranslatorConfig", summary = "Creates a configuration for Google translator.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> createGoogleTranslator(@RequestBody GoogleTranslatorConfigDto config) {
        return manager
                .create(configProvider.createConfig(config))
                .map(mapper::mapToDto);
    }

    /**
     * Creates a configuration for Azure translator.
     */
    @PostMapping(path = "/external-translator/azure")
    @Operation(operationId = "createAzureTranslatorConfig", summary = "Creates a configuration for Azure translator.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> createITranslateTranslator(@RequestBody AzureTranslatorConfigDto config) {
        return manager
                .create(configProvider.createConfig(config))
                .map(mapper::mapToDto);
    }

    /**
     * Creates a configuration for iTranslate.
     */
    @PostMapping(path = "/external-translator/iTranslate")
    @Operation(operationId = "createITranslateConfig", summary = "Creates a configuration for iTranslate.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> createITranslateTranslator(@RequestBody ITranslateTranslatorConfigDto config) {
        return manager
                .create(configProvider.createConfig(config))
                .map(mapper::mapToDto);
    }

    /**
     * Updates an external translator configuration.
     */
    @PutMapping(path = "/external-translator/{id}")
    @Operation(operationId = "update", summary = "Updates an external translator configuration.")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Mono<ExternalTranslatorConfigDto> update(@PathVariable String id,
                                                    @RequestBody ExternalTranslatorConfigDto config) {
        if (!Objects.equals(id, config.getId())) {
            return Mono.error(BadRequestException.idRequestNotMatchIdBodyException(id, config.getId()));
        }

        return manager.update(config)
                .map(mapper::mapToDto);
    }

    /**
     * Deletes the configuration having the specified id.
     */
    @DeleteMapping(path = "/external-translator/{id}")
    @Operation(operationId = "delete", summary = "Deletes the configuration having the specified id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public Mono<Void> delete(@PathVariable String id) {
        return manager.delete(id).then();
    }
}
