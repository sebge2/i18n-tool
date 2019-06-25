package be.sgerard.i18n.model.i18n.event;

import be.sgerard.i18n.model.i18n.dto.BundleKeyTranslationDto;
import be.sgerard.i18n.model.i18n.dto.WorkspaceDto;
import be.sgerard.i18n.model.security.user.UserDto;

import java.util.List;


/**
 * @author Sebastien Gerard
 */
public class TranslationsUpdateEventDto {

    private final WorkspaceDto workspace;
    private final UserDto user;
    private final List<BundleKeyTranslationDto> updatedEntries;

    public TranslationsUpdateEventDto(WorkspaceDto workspace,
                                      UserDto user,
                                      List<BundleKeyTranslationDto> updatedEntries) {
        this.workspace = workspace;
        this.user = user;
        this.updatedEntries = updatedEntries;
    }

    public WorkspaceDto getWorkspace() {
        return workspace;
    }

    public UserDto getUser() {
        return user;
    }

    public List<BundleKeyTranslationDto> getUpdatedEntries() {
        return updatedEntries;
    }
}
