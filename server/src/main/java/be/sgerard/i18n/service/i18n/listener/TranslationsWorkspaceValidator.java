package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.TranslationUpdateDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsWorkspaceValidator implements TranslationsListener {

    @Override
    public Mono<ValidationResult> beforeUpdate(TranslationUpdateDto translationUpdate) {
//        final WorkspaceEntity workspace = translation.getBundleKey().getBundleFile().getWorkspace();
//
//        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
//            throw BadRequestException.actionNotAllowedInStateException(WorkspaceStatus.INITIALIZED.name(), workspace.getStatus().name());
        }
// TODO
        return Mono.just(ValidationResult.EMPTY);
    }
}
