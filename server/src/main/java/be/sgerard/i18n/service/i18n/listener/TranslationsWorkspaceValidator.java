package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.model.workspace.WorkspaceStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsWorkspaceValidator implements TranslationsListener {

    @Override
    public Mono<ValidationResult> beforeUpdate(BundleKeyTranslationEntity translation, String updatedValue) {
        final WorkspaceEntity workspace = translation.getBundleKey().getBundleFile().getWorkspace();

        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
//            throw BadRequestException.actionNotAllowedInStateException(WorkspaceStatus.INITIALIZED.name(), workspace.getStatus().name());
        }
// TODO
        return Mono.just(ValidationResult.EMPTY);
    }
}
