package be.sgerard.i18n.model.i18n.dto;

import be.sgerard.i18n.model.security.user.dto.UserDto;

import java.util.List;


/**
 * @author Sebastien Gerard
 */
public class TranslationsUpdateEventDto {

    private final UserDto user;
    private final List<BundleKeyTranslationDto> updatedEntries;

    public TranslationsUpdateEventDto(UserDto user, List<BundleKeyTranslationDto> updatedEntries) {
        this.user = user;
        this.updatedEntries = updatedEntries;
    }
// TODO
    public UserDto getUser() {
        return user;
    }

    public List<BundleKeyTranslationDto> getUpdatedEntries() {
        return updatedEntries;
    }
}
