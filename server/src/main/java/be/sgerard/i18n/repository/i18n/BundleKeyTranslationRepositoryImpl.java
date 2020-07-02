package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.TranslationsSearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.security.auth.AuthenticatedUser;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import java.lang.reflect.Constructor;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*;

/**
 * Implementation of the {@link BundleKeyTranslationRepositoryCustom custom translation repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class BundleKeyTranslationRepositoryImpl implements BundleKeyTranslationRepositoryCustom {

    /**
     * @see BundleKeyTranslationEntity#getWorkspace()
     */
    public static final String FIELD_WORKSPACE = "workspace";

    /**
     * @see BundleKeyTranslationEntity#getOriginalValue()
     */
    public static final String FIELD_ORIGINAL_VALUE = "originalValue";

    /**
     * @see BundleKeyTranslationEntity#getUpdatedValue()
     */
    public static final String FIELD_UPDATED_VALUE = "updatedValue";

    /**
     * @see BundleKeyTranslationEntity#getLastEditor()
     */
    public static final String FIELD_LAST_EDITOR = "lastEditor";

    /**
     * @see BundleKeyTranslationEntity#getBundleKey()
     */
    public static final String FIELD_BUNDLE_KEY = "bundleKey";

    /**
     * @see BundleKeyTranslationEntity#getBundleFile()
     */
    public static final String FIELD_BUNDLE_ID = "bundleId";

    /**
     * @see BundleKeyTranslationEntity#getLocale()
     */
    public static final String FIELD_LOCALE = "locale";

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
    public Flux<BundleKeyTranslationEntity> search(TranslationsSearchRequestDto request) {
        return authenticationManager
                .getCurrentUserOrDie()
                .flatMapMany(currentUser -> template.find(createQuery(request, currentUser), BundleKeyTranslationEntity.class));
    }

    /**
     * Creates the {@link Query query} for the specified {@link TranslationsSearchRequestDto request}.
     */
    private Query createQuery(TranslationsSearchRequestDto request, AuthenticatedUser currentUser) {
        final Query query = new Query();

        if (!request.getWorkspaces().isEmpty()) {
            query.addCriteria(Criteria.where(FIELD_WORKSPACE).in(request.getWorkspaces()));
        }

        if (!request.getLocales().isEmpty()) {
            query.addCriteria(Criteria.where("locale").in(request.getLocales()));
        }

        request.getLastKey()
                .ifPresent(lastKey -> query.addCriteria(Criteria.where("id").gt(lastKey)));

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
                case CONTAINS:
                    query.addCriteria(createCriteriaOnKey(pattern, contains().ignoreCase()));
                    break;
                case ENDS_WITH:
                    query.addCriteria(createCriteriaOnKey(pattern, endsWith().ignoreCase()));
                    break;
                case STARTS_WITH:
                    query.addCriteria(createCriteriaOnKey(pattern, startsWith().ignoreCase()));
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported pattern strategy [" + pattern.getStrategy() + "].");
            }
        }

        return query
                .with(Sort.by(FIELD_WORKSPACE, FIELD_BUNDLE_ID, FIELD_BUNDLE_KEY, FIELD_LOCALE))
                .limit(request.getMaxKeys());
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link #FIELD_BUNDLE_KEY bundle key} field.
     */
    private Criteria createCriteriaOnKey(TranslationKeyPatternDto pattern, ExampleMatcher.GenericPropertyMatcher matcher) {
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
