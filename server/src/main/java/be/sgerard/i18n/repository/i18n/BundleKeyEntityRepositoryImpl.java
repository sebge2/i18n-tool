package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.TranslationsSearchRequest;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationKeyPatternDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationSearchCriterion;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationValueRestrictionDto;
import be.sgerard.i18n.model.i18n.dto.translation.key.TranslationsSearchPageSpecDto;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationEntity;
import be.sgerard.i18n.model.i18n.persistence.BundleKeyTranslationModificationEntity;
import org.bson.BsonRegularExpression;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.MongoRegexCreator;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Comparator;
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
    private final Field translationOriginalValueField;
    private final Field translationModificationField;

    public BundleKeyEntityRepositoryImpl(ReactiveMongoTemplate template) throws Exception {
        this.template = template;

        this.bundleKeyConstructor = BundleKeyEntity.class.getDeclaredConstructor();
        this.bundleKeyConstructor.setAccessible(true);

        this.translationConstructor = BundleKeyTranslationEntity.class.getDeclaredConstructor();
        this.translationConstructor.setAccessible(true);

        this.translationOriginalValueField = BundleKeyTranslationEntity.class.getDeclaredField("originalValue");
        this.translationOriginalValueField.setAccessible(true);

        this.translationModificationField = BundleKeyTranslationEntity.class.getDeclaredField("modification");
        this.translationModificationField.setAccessible(true);

        this.modificationEntityConstructor = BundleKeyTranslationModificationEntity.class.getDeclaredConstructor();
        this.modificationEntityConstructor.setAccessible(true);
    }

    @Override
    public Flux<BundleKeyEntity> search(TranslationsSearchRequest request) {
        Flux<BundleKeyEntity> entities = search(createQuery(request));

        if (!request.getPageSpec().map(TranslationsSearchPageSpecDto::isNextPage).orElse(true)) {
            entities = entities
                    .sort(Comparator.comparing(BundleKeyEntity::getSortingKey));
        }

        return entities;
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

        request.getKeyPattern()
                .ifPresent(keyPattern -> query.addCriteria(createKeyPatternCriteria(keyPattern)));

        request.getValueRestriction()
                .ifPresent(valueRestriction -> query.addCriteria(createValueCriteria(valueRestriction, request.getLocales())));

        final Criteria[] translationsCriteria = createCriteria(request.getCriterion(), request.getLocales(), request.getCurrentUser());
        if (translationsCriteria.length > 0) {
            query.addCriteria(new Criteria().orOperator(translationsCriteria));
        }

        request.getMaxKeys().ifPresent(query::limit);

        request.getPageSpec().ifPresent(pageSpec -> {
            if (pageSpec.isNextPage()) {
                query.addCriteria(Criteria.where(FIELD_SORTING_KEY).gt(pageSpec.getKeyOtherPage()));
            } else {
                query.addCriteria(Criteria.where(FIELD_SORTING_KEY).lt(pageSpec.getKeyOtherPage()));
            }
        });

        final Sort sort = request.getPageSpec().map(TranslationsSearchPageSpecDto::isNextPage).orElse(true)
                ? Sort.by(FIELD_SORTING_KEY).ascending()
                : Sort.by(FIELD_SORTING_KEY).descending();

        return query.with(sort);
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
            case EQUALS:
                return createCriteriaOnBundleKey(pattern, MongoRegexCreator.MatchMode.EXACT);
            case CONTAINS:
                return createCriteriaOnBundleKey(pattern, MongoRegexCreator.MatchMode.CONTAINING);
            case ENDS_WITH:
                return createCriteriaOnBundleKey(pattern, MongoRegexCreator.MatchMode.ENDING_WITH);
            case STARTS_WITH:
                return createCriteriaOnBundleKey(pattern, MongoRegexCreator.MatchMode.STARTING_WITH);
            default:
                throw new UnsupportedOperationException("Unsupported pattern strategy [" + pattern.getStrategy() + "].");
        }
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link TranslationValueRestrictionDto value}.
     */
    private Criteria createValueCriteria(TranslationValueRestrictionDto valueRestriction, List<String> locales) {
        switch (valueRestriction.getStrategy()) {
            case EQUALS:
                return createCriteriaOnTranslationValue(valueRestriction, exact().ignoreCase(), locales);
            case CONTAINS:
                return createCriteriaOnTranslationValue(valueRestriction, contains().ignoreCase(), locales);
            case ENDS_WITH:
                return createCriteriaOnTranslationValue(valueRestriction, endsWith().ignoreCase(), locales);
            case STARTS_WITH:
                return createCriteriaOnTranslationValue(valueRestriction, startsWith().ignoreCase(), locales);
            default:
                throw new UnsupportedOperationException("Unsupported pattern strategy [" + valueRestriction.getStrategy() + "].");
        }
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the {@link BundleKeyEntityRepository#FIELD_BUNDLE_KEY bundle key} field
     * based on the specified  matching {@link MongoRegexCreator.MatchMode mode}.
     */
    private Criteria createCriteriaOnBundleKey(TranslationKeyPatternDto pattern, MongoRegexCreator.MatchMode matchMode) {
        final String regex = MongoRegexCreator.INSTANCE.toRegularExpression(pattern.getPattern(), matchMode);

        if (regex == null) {
            throw new IllegalStateException("The regex cannot be null at this point. Hint: please check the algorithm.");
        }

        return Criteria.where(FIELD_BUNDLE_KEY).regex(new BsonRegularExpression(regex, "i"));
    }

    /**
     * Creates a {@link Criteria criteria} for filtering the keys based on their translation values.
     */
    private Criteria createCriteriaOnTranslationValue(TranslationValueRestrictionDto valueRestriction,
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

                                final BundleKeyTranslationModificationEntity modification = modificationEntityConstructor.newInstance();
                                modification.setUpdatedValue(valueRestriction.getTranslation());

                                final BundleKeyTranslationEntity bundleKeyTranslation = translationConstructor.newInstance();
                                translationOriginalValueField.set(bundleKeyTranslation, valueRestriction.getTranslation());
                                translationModificationField.set(bundleKeyTranslation, modification);

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
