package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import java.lang.reflect.Constructor;

import static be.sgerard.i18n.repository.i18n.BundleKeyTranslationRepository.*;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*;

/**
 * Implementation of the {@link BundleKeyTranslationRepositoryCustom custom translation repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class BundleKeyTranslationRepositoryImpl implements BundleKeyTranslationRepositoryCustom {

    private final ReactiveMongoTemplate template;
    private final AuthenticationManager authenticationManager;
    private final Constructor<BundleKeyTranslationEntity> entityConstructor;

    public BundleKeyTranslationRepositoryImpl(ReactiveMongoTemplate template, AuthenticationManager authenticationManager) throws Exception {
        this.template = template;
        this.authenticationManager = authenticationManager;

        this.entityConstructor = BundleKeyTranslationEntity.class.getDeclaredConstructor();
        this.entityConstructor.setAccessible(true);
    }

    @Override
    public Flux<BundleKeyTranslationEntity> search(TranslationsSearchRequest request) {
        return authenticationManager
                .getCurrentUserOrDie()
                .flatMapMany(currentUser -> search(createQuery(request, currentUser)));
    }

    @Override
    public Flux<BundleKeyTranslationEntity> search(Query query) {
        return template.find(query, BundleKeyTranslationEntity.class);
    }

    /**
     * Creates the {@link Query query} for the specified {@link TranslationsSearchRequest request}.
     */
    private Query createQuery(TranslationsSearchRequest request, AuthenticatedUser currentUser) {
        final Query query = new Query();

        if (!request.getWorkspaces().isEmpty()) {
            query.addCriteria(Criteria.where(FIELD_WORKSPACE).in(request.getWorkspaces()));
        }

        if (!request.getLocales().isEmpty()) {
            query.addCriteria(Criteria.where(FIELD_LOCALE).in(request.getLocales()));
        }

        switch (request.getCriterion()) {
            case MISSING_TRANSLATIONS:
                query.addCriteria(Criteria.where(FIELD_ORIGINAL_VALUE).is(null));
                query.addCriteria(Criteria.where(FIELD_UPDATED_VALUE).is(null));
                break;
            case UPDATED_TRANSLATIONS:
                query.addCriteria(Criteria.where(FIELD_UPDATED_VALUE).is(null).not());
                break;
            case TRANSLATIONS_CURRENT_USER_UPDATED:
                query.addCriteria(Criteria.where(FIELD_UPDATED_VALUE).is(null).not());
                query.addCriteria(Criteria.where(FIELD_LAST_EDITOR).is(currentUser.getUser().getId()));
                break;
        }

        if (request.getKeyPattern().isPresent()) {
            final TranslationKeyPatternDto pattern = request.getKeyPattern().get();

            switch (pattern.getStrategy()) {
                case EQUAL:
                    query.addCriteria(createCriteriaOnBundleKey(pattern, exact().ignoreCase()));
                    break;
                case CONTAINS:
                    query.addCriteria(createCriteriaOnBundleKey(pattern, contains().ignoreCase()));
                    break;
                case ENDS_WITH:
                    query.addCriteria(createCriteriaOnBundleKey(pattern, endsWith().ignoreCase()));
                    break;
                case STARTS_WITH:
                    query.addCriteria(createCriteriaOnBundleKey(pattern, startsWith().ignoreCase()));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported pattern strategy [" + pattern.getStrategy() + "].");
            }
        }

        return query
                .with(PageRequest.of(request.getPageIndex(), request.getMaxTranslations(), Sort.by(request.getSortBy().toArray(new String[0]))));
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link BundleKeyTranslationRepository#FIELD_BUNDLE_KEY bundle key} field.
     */
    private Criteria createCriteriaOnBundleKey(TranslationKeyPatternDto pattern, ExampleMatcher.GenericPropertyMatcher matcher) {
        try {
            final BundleKeyTranslationEntity entity = entityConstructor.newInstance();
            entity.setBundleKey(pattern.getPattern());

            final ExampleMatcher name = ExampleMatcher.matching().withMatcher(FIELD_BUNDLE_KEY, matcher);

            return Criteria.byExample(Example.of(entity, name));
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing the entity.", e);
        }
    }
}
