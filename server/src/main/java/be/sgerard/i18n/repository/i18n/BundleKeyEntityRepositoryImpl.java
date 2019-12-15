package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.TranslationValueRestrictionDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static be.sgerard.i18n.repository.i18n.BundleKeyEntityRepository.*;
import static java.util.Collections.singletonMap;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.*;

/**
 * Implementation of the {@link BundleKeyEntityRepositoryCustom bundle key repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class BundleKeyEntityRepositoryImpl implements BundleKeyEntityRepositoryCustom {

    private final ReactiveMongoTemplate template;
    private final Constructor<BundleKeyEntity> bundleKeyConstructor;
    private final Constructor<BundleKeyTranslationEntity> translationConstructor;
    private final Constructor<BundleKeyTranslationModificationEntity> modificationEntityConstructor;

    public BundleKeyEntityRepositoryImpl(ReactiveMongoTemplate template) throws Exception {
        this.template = template;

        this.bundleKeyConstructor = BundleKeyEntity.class.getDeclaredConstructor();
        this.bundleKeyConstructor.setAccessible(true);

        this.translationConstructor = BundleKeyTranslationEntity.class.getDeclaredConstructor();
        this.translationConstructor.setAccessible(true);

        this.modificationEntityConstructor = BundleKeyTranslationModificationEntity.class.getDeclaredConstructor();
        this.modificationEntityConstructor.setAccessible(true);
    }

    @Override
    public Flux<BundleKeyEntity> search(TranslationsSearchRequest request) {
        return search(createQuery(request));
    }

    @Override
    public Flux<BundleKeyEntity> search(Query query) {
        return template.find(query, BundleKeyEntity.class);
    }

    /**
     * Creates the {@link Query query} for the specified {@link TranslationsSearchRequest request}.
     */
    private Query createQuery(TranslationsSearchRequest request) {
        final Query query = new Query();

        if (!request.getWorkspaces().isEmpty()) {
            query.addCriteria(Criteria.where(FIELD_WORKSPACE).in(request.getWorkspaces()));
        }

        if (!request.getBundleFiles().isEmpty()) {
            query.addCriteria(Criteria.where(FIELD_BUNDLE_FILE).in(request.getBundleFiles()));
        }

        if (request.getKeyPattern().isPresent()) {
            query.addCriteria(createKeyPatternCriteria(request.getKeyPattern().get()));
        }

        if (request.getValueRestriction().isPresent()) {
            query.addCriteria(createValueCriteria(request.getValueRestriction().get(), request.getLocales()));
        }

        final Criteria[] translationsCriteria = createCriteria(request.getCriterion(), request.getLocales(), request.getCurrentUser());
        if (translationsCriteria.length > 0) {
            query.addCriteria(new Criteria().orOperator(translationsCriteria));
        }

        if (request.getLastPageKey().isPresent()) {
            query.addCriteria(Criteria.where(FIELD_SORTING_KEY).gt(request.getLastPageKey().get()));
        }

        request.getMaxKeys().ifPresent(query::limit);

        return query
                .with(Sort.by(FIELD_SORTING_KEY));
    }

    /**
     * Creates a {@link Criteria criteria} for filtering based on the specified {@link TranslationSearchCriterion criterion}.
     */
    private Criteria[] createCriteria(TranslationSearchCriterion criterion, List<String> locales, String currentUser) {
        return locales.stream()
                .map(locale -> {
                    switch (criterion) {
                        case MISSING_TRANSLATIONS:
                            return new Criteria().andOperator(
                                    Criteria.where(getTranslationField(locale, TRANSLATION_FIELD_ORIGINAL_VALUE)).is(null),
                                    Criteria.where(getTranslationField(locale, TRANSLATION_FIELD_UPDATED_VALUE)).is(null)
                            );
                        case UPDATED_TRANSLATIONS:
                            return Criteria.where(getTranslationField(locale, TRANSLATION_FIELD_MODIFICATION)).ne(null);
                        case TRANSLATIONS_CURRENT_USER_UPDATED:
                            return Criteria.where(getTranslationField(locale, TRANSLATION_FIELD_LAST_EDITOR)).is(currentUser);
                        case ALL:
                            return null;
                        default:
                            throw new UnsupportedOperationException("Unsupported criterion [" + criterion + "].");
                    }
                })
                .filter(Objects::nonNull)
                .toArray(Criteria[]::new);
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link TranslationKeyPatternDto pattern}.
     */
    private Criteria createKeyPatternCriteria(TranslationKeyPatternDto pattern) {
        switch (pattern.getStrategy()) {
            case EQUAL:
                return createCriteriaOnBundleKey(pattern, exact().ignoreCase());
            case CONTAINS:
                return createCriteriaOnBundleKey(pattern, contains().ignoreCase());
            case ENDS_WITH:
                return createCriteriaOnBundleKey(pattern, endsWith().ignoreCase());
            case STARTS_WITH:
                return createCriteriaOnBundleKey(pattern, startsWith().ignoreCase());
            default:
                throw new UnsupportedOperationException("Unsupported pattern strategy [" + pattern.getStrategy() + "].");
        }
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link TranslationValueRestrictionDto value}.
     */
    private Criteria createValueCriteria(TranslationValueRestrictionDto valueRestriction, List<String> locales) {
        switch (valueRestriction.getStrategy()) {
            case EQUAL:
                return createCriteriaOnTranslationKey(valueRestriction, exact().ignoreCase(), locales);
            case CONTAINS:
                return createCriteriaOnTranslationKey(valueRestriction, contains().ignoreCase(), locales);
            case ENDS_WITH:
                return createCriteriaOnTranslationKey(valueRestriction, endsWith().ignoreCase(), locales);
            case STARTS_WITH:
                return createCriteriaOnTranslationKey(valueRestriction, startsWith().ignoreCase(), locales);
            default:
                throw new UnsupportedOperationException("Unsupported pattern strategy [" + valueRestriction.getStrategy() + "].");
        }
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link BundleKeyEntityRepository#FIELD_BUNDLE_KEY bundle key} field.
     */
    private Criteria createCriteriaOnBundleKey(TranslationKeyPatternDto pattern, ExampleMatcher.GenericPropertyMatcher matcher) {
        try {
            final BundleKeyEntity entity = bundleKeyConstructor.newInstance();
            entity.setKey(pattern.getPattern());

            final ExampleMatcher name = ExampleMatcher.matchingAny().withMatcher(FIELD_BUNDLE_KEY, matcher);

            return Criteria.byExample(Example.of(entity, name));
        } catch (Exception e) {
            throw new IllegalStateException("Error while initializing the entity.", e);
        }
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link BundleKeyEntityRepository#FIELD_BUNDLE_KEY bundle key} field.
     */
    private Criteria createCriteriaOnTranslationKey(TranslationValueRestrictionDto valueRestriction,
                                                    ExampleMatcher.GenericPropertyMatcher matcher,
                                                    List<String> allLocales) {

        return new Criteria().orOperator(
                valueRestriction.getLocale()
                        .map(Collections::singletonList)
                        .orElse(allLocales)
                        .stream()
                        .map(locale -> {
                            try {
                                final BundleKeyEntity bundleKey = bundleKeyConstructor.newInstance();

                                final BundleKeyTranslationEntity bundleKeyTranslation = translationConstructor.newInstance();
                                bundleKeyTranslation.setOriginalValue(valueRestriction.getTranslation());

                                final BundleKeyTranslationModificationEntity modification = modificationEntityConstructor.newInstance();
                                modification.setUpdatedValue(valueRestriction.getTranslation());

                                bundleKeyTranslation.setModification(modification);

                                bundleKey.setTranslations(singletonMap(locale, bundleKeyTranslation));
                                final ExampleMatcher value = ExampleMatcher.matchingAny()
                                        .withMatcher(getTranslationField(locale, TRANSLATION_FIELD_ORIGINAL_VALUE), matcher)
                                        .withMatcher(getTranslationField(locale, TRANSLATION_FIELD_UPDATED_VALUE), matcher);

                                return Criteria.byExample(Example.of(bundleKey, value));
                            } catch (Exception e) {
                                throw new IllegalStateException("Error while initializing the entity.", e);
                            }
                        })
                        .toArray(Criteria[]::new)
        );
    }
}
