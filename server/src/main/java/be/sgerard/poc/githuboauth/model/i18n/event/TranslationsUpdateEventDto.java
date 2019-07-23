package be.sgerard.poc.githuboauth.model.i18n.event;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeyEntryDto;
import be.sgerard.poc.githuboauth.model.i18n.dto.WorkspaceDto;
import be.sgerard.poc.githuboauth.model.security.user.UserDto;

import java.util.List;


/**
 * @author Sebastien Gerard
 */
public class TranslationsUpdateEventDto {

    private final WorkspaceDto workspace;
    private final UserDto user;
    private final List<BundleKeyEntryDto> updatedEntries;

    public TranslationsUpdateEventDto(WorkspaceDto workspace,
                                      UserDto user,
                                      List<BundleKeyEntryDto> updatedEntries) {
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

    public List<BundleKeyEntryDto> getUpdatedEntries() {
        return updatedEntries;
    }
}
