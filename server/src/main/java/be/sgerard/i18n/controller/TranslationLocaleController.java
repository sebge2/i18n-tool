package be.sgerard.i18n.controller;

import be.sgerard.i18n.model.i18n.dto.TranslationLocaleCreationDto;
import be.sgerard.i18n.model.i18n.dto.TranslationLocaleDto;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.i18n.TranslationLocaleManager;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * Controller managing all locales used by the application to translate.
 *
 * @author Sebastien Gerard
 */
@RestController
@RequestMapping(path = "/api")
@Api(value = "Controller handling locales registered by end-users.")
public class TranslationLocaleController {

    private final TranslationLocaleManager localeManager;

    public TranslationLocaleController(TranslationLocaleManager localeManager) {
        this.localeManager = localeManager;
    }

    /**
     * Returns all the registered {@link TranslationLocaleDto locales}.
     */
    @GetMapping(path = "/translation/locale/")
    @ApiOperation(value = "Returns all translation locales that have been used so far.")
    public Flux<TranslationLocaleDto> findAll() {
        return localeManager
                .findAll()
                .map(locale -> TranslationLocaleDto.builder(locale).build());
    }

    /**
     * Returns the registered {@link TranslationLocaleDto locale}.
     */
    @GetMapping(path = "/translation/locale/{id}")
    @ApiOperation(value = "Returns the registered locale.")
    public Mono<TranslationLocaleDto> findById(@PathVariable String id) {
        return localeManager
                .findByIdOrDie(id)
                .map(locale -> TranslationLocaleDto.builder(locale).build());
    }

    /**
     * Creates a new {@link TranslationLocaleDto translation locale}.
     */
    @PostMapping(path = "/translation/locale")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates a new translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<TranslationLocaleDto> create(@RequestBody TranslationLocaleCreationDto creationDto) {
        return localeManager
                .create(creationDto)
                .map(locale -> TranslationLocaleDto.builder(locale).build());
    }

    /**
     * Updates a {@link TranslationLocaleDto translation locale}.
     */
    @PutMapping(path = "/translation/locale/{id}")
    @ApiOperation(value = "Updates an existing translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<TranslationLocaleDto> update(@PathVariable String id,
                                             @RequestBody TranslationLocaleDto localeDto) {
        if (!Objects.equals(id, localeDto.getId())) {
            return Mono.error(BadRequestException.idRequestNotMatchIdBodyException(id, localeDto.getId()));
        }

        return localeManager
                .update(localeDto)
                .map(locale -> TranslationLocaleDto.builder(locale).build());
    }

    /**
     * Deletes a {@link TranslationLocaleDto translation locale}.
     */
    @DeleteMapping(path = "/translation/locale/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes a translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public Mono<Void> delete(@PathVariable String id) {
        return localeManager.delete(id).then();
    }

}
