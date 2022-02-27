package be.sgerard.i18n.service.i18n.listener;

import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationUpdateDto;
import be.sgerard.i18n.model.validation.ValidationResult;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collection;

/**
 * @author Sebastien Gerard
 */
@Component
public class TranslationsWorkspaceValidator implements TranslationsListener {

    @Override
    public Mono<ValidationResult> beforeUpdate(Collection<TranslationUpdateDto> translationUpdates) {
//        final WorkspaceEntity workspace = translation.getBundleKey().getBundleFile().getWorkspace();
//
//        if (workspace.getStatus() != WorkspaceStatus.INITIALIZED) {
//            throw BadRequestException.actionNotAllowedInStateException(WorkspaceStatus.INITIALIZED.name(), workspace.getStatus().name());
//        }
// TODO
        return Mono.just(ValidationResult.EMPTY);
    }
}
