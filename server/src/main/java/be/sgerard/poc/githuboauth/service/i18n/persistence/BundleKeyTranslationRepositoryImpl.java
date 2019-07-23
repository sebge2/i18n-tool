package be.sgerard.poc.githuboauth.service.i18n.persistence;

import be.sgerard.poc.githuboauth.model.i18n.dto.BundleKeyEntrySearchRequestDto;
import be.sgerard.poc.githuboauth.model.i18n.persistence.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.Locale;
import java.util.stream.Stream;

/**
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class BundleKeyTranslationRepositoryImpl implements BundleKeyTranslationRepositoryCustom {

    public static final String HINT_FETCH_GRAPH = "javax.persistence.fetchgraph";

    private final EntityManager entityManager;

    public BundleKeyTranslationRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Stream<BundleKeyTranslationEntity> searchEntries(BundleKeyEntrySearchRequestDto request) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        final CriteriaQuery<BundleKeyTranslationEntity> query = criteriaBuilder.createQuery(BundleKeyTranslationEntity.class);

        final Root<BundleKeyTranslationEntity> selectEntry = query.from(BundleKeyTranslationEntity.class);
        final Join<BundleKeyTranslationEntity, BundleKeyEntity> bundleKeyJoin = selectEntry.join(BundleKeyTranslationEntity_.bundleKey);
        final Join<BundleKeyEntity, BundleFileEntity> bundleFileJoin = bundleKeyJoin.join(BundleKeyEntity_.bundleFile);
        final Join<BundleFileEntity, WorkspaceEntity> workspaceJoin = bundleFileJoin.join(BundleFileEntity_.workspace);

        query.select(selectEntry);

        final Predicate whereClause = criteriaBuilder.conjunction();

        whereClause.getExpressions().add(workspaceJoin.get(WorkspaceEntity_.id).in(request.getWorkspaceId()));

        request.getLastKey()
                .ifPresent(lastKey -> whereClause.getExpressions().add(criteriaBuilder.greaterThan(bundleKeyJoin.get(BundleKeyEntity_.id), lastKey)));

        if (!request.getMissingLocales().isEmpty()) {
            whereClause.getExpressions().add(
                    selectEntry.get(BundleKeyTranslationEntity_.locale).in(request.getMissingLocales().stream().map(Locale::toString).toArray())
            );

            whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.originalValue).isNull());
            whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNull());
        }

        if (!request.getLocales().isEmpty()) {
            whereClause.getExpressions().add(
                    selectEntry.get(BundleKeyTranslationEntity_.locale).in(request.getLocales().stream().map(Locale::toString).toArray())
            );
        }

        request.getKeyPattern()
                .ifPresent(keyPattern ->
                        whereClause.getExpressions().add(criteriaBuilder.like(bundleKeyJoin.get(BundleKeyEntity_.key), keyPattern))
                );

        request.hasBeenUpdated()
                .ifPresent(hasBeenUpdated ->
                        whereClause.getExpressions().add(
                                hasBeenUpdated
                                        ? selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNotNull()
                                        : selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNull()
                        )
                );

        if (!whereClause.getExpressions().isEmpty()) {
            query.where(whereClause);
        }

        query.orderBy(
                criteriaBuilder.asc(bundleKeyJoin.get(BundleKeyEntity_.bundleFile)),
                criteriaBuilder.asc(selectEntry.get(BundleKeyTranslationEntity_.bundleKey)),
                criteriaBuilder.asc(selectEntry.get(BundleKeyTranslationEntity_.locale))
        );

        return entityManager
                .createQuery(query)
                .setMaxResults(request.getMaxKeyEntries())
                .setHint(HINT_FETCH_GRAPH, entityManager.getEntityGraph(BundleKeyTranslationEntity.GRAPH_FETCH_ENTRIES_TO_WORKSPACE))
                .getResultStream();
    }
}
