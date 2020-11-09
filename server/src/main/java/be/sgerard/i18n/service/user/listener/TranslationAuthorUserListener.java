package be.sgerard.i18n.service.user.listener;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.security.user.persistence.UserEntity;
import be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

/**
 * {@link UserListener User listener} removing links to the deleted user, contained in modified translations.
 *
 * @author Sebastien Gerard
 */
@Component
public class TranslationAuthorUserListener implements UserListener {

    private final BundleKeyEntityRepository translationRepository;

    public TranslationAuthorUserListener(BundleKeyEntityRepository translationRepository) {
        this.translationRepository = translationRepository;
    }

    @Override
    public Mono<Void> afterDelete(UserEntity user) {
        return translationRepository
                .search(
                        TranslationsSearchRequest.builder()
                                .currentUser(user.getId())
                                .criterion(TranslationSearchCriterion.TRANSLATIONS_CURRENT_USER_UPDATED)
                                .build()
                )
                .flatMap(translationRepository::delete)
                .then();
    }
}
