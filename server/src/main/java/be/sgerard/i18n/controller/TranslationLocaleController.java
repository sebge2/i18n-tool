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

import java.util.Collection;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

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
    public Collection<TranslationLocaleDto> findAll() {
        return localeManager.findAll().stream()
                .map(locale -> TranslationLocaleDto.builder(locale).build())
                .collect(toList());
    }

    /**
     * Returns the registered {@link TranslationLocaleDto locale}.
     */
    @GetMapping(path = "/translation/locale/{id}")
    @ApiOperation(value = "Returns the registered locale.")
    public TranslationLocaleDto findById(@PathVariable String id) {
        return TranslationLocaleDto.builder(localeManager.findById(id)).build();
    }

    /**
     * Creates a new {@link TranslationLocaleDto translation locale}.
     */
    @PostMapping(path = "/translation/locale")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Creates a new translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public TranslationLocaleDto create(@RequestBody TranslationLocaleCreationDto locale) {
        return TranslationLocaleDto.builder(localeManager.create(locale)).build();
    }

    /**
     * Updates a {@link TranslationLocaleDto translation locale}.
     */
    @PutMapping(path = "/translation/locale/{id}")
    @ApiOperation(value = "Updates an existing translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public TranslationLocaleDto update(@PathVariable String id,
                                       @RequestBody TranslationLocaleDto locale) {
        if (!Objects.equals(id, locale.getId())) {
            throw BadRequestException.idRequestNotMatchIdBodyException(id, locale.getId());
        }

        return TranslationLocaleDto.builder(localeManager.update(locale)).build();
    }

    /**
     * Deletes a {@link TranslationLocaleDto translation locale}.
     */
    @DeleteMapping(path = "/translation/locale/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation(value = "Deletes a translation locale.")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable String id) {
        localeManager.delete(id);
    }

}
