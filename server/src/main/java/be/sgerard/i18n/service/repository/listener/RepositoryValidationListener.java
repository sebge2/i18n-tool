package be.sgerard.i18n.service.repository.listener;

import be.sgerard.i18n.model.repository.RepositoryStatus;
import be.sgerard.i18n.model.repository.dto.RepositoryPatchDto;
import be.sgerard.i18n.model.repository.persistence.RepositoryEntity;
import be.sgerard.i18n.model.validation.ValidationMessage;
import be.sgerard.i18n.model.validation.ValidationResult;
import be.sgerard.i18n.repository.repository.RepositoryEntityRepository;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * {@link RepositoryListener Listener} checking that repositories are valid.
 *
 * @author Sebastien Gerard
 */
@Component
public class RepositoryValidationListener implements RepositoryListener<RepositoryEntity> {

    /**
     * Validation message key specifying that the repository name is not unique.
     */
    public static final String NAME_NOT_UNIQUE = "validation.repository.name-not-unique";

    /**
     * Validation message key specifying that the repository cannot be edited.
     */
    public static final String READ_ONLY = "validation.repository.read-only";

    private final RepositoryEntityRepository repository;

    public RepositoryValidationListener(RepositoryEntityRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean support(RepositoryEntity repositoryEntity) {
        return true;
    }

    @Override
    public ValidationResult beforePersist(RepositoryEntity repositoryEntity) {
        if (repository.findAll().stream().anyMatch(repo -> Objects.equals(repo.getName(), repositoryEntity.getName()))) {
            return ValidationResult.builder()
                    .messages(new ValidationMessage(NAME_NOT_UNIQUE, repositoryEntity.getName()))
                    .build();
        }

        return ValidationResult.EMPTY;
    }

    @Override
    public ValidationResult beforeUpdate(RepositoryEntity repositoryEntity, RepositoryPatchDto patch) {
        if((repositoryEntity.getStatus() != RepositoryStatus.NOT_INITIALIZED) && (repositoryEntity.getStatus() != RepositoryStatus.INITIALIZED)){
            return ValidationResult.builder()
                    .messages(new ValidationMessage(READ_ONLY))
                    .build();
        }

        return ValidationResult.EMPTY;
    }
}
