package be.sgerard.i18n.service.repository.validation;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.service.repository.RepositoryManager;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * {@link RepositoryValidator Validator} checking that repositories are valid.
 *
 * @author Sebastien Gerard
 */
@Component
public class GlobalRepositoryValidator implements RepositoryValidator<RepositoryEntity> {

    /**
     * Validation message key specifying that the repository name is not unique.
     */
    public static final String NAME_NOT_UNIQUE = "validation.repository.name-not-unique";

    private final RepositoryManager repositoryManager;

    public GlobalRepositoryValidator(RepositoryManager repositoryManager) {
        this.repositoryManager = repositoryManager;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public Mono<ValidationResult> beforePersist(RepositoryEntity repositoryEntity) {
        return repositoryManager
                .findAll()
                .filter(repo -> Objects.equals(repo.getName(), repositoryEntity.getName()))
                .collectList()
                .map(conflicts -> {
                    if (!conflicts.isEmpty()) {
                        return ValidationResult.singleMessage(new ValidationMessage(NAME_NOT_UNIQUE, repositoryEntity.getName()));
                    } else {
                        return ValidationResult.EMPTY;
                    }
                });
    }
}
