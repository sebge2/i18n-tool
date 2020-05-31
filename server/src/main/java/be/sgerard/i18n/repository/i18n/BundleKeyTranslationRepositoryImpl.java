package be.sgerard.i18n.repository.i18n;

import be.sgerard.i18n.model.i18n.dto.BundleKeyEntrySearchRequestDto;
import be.sgerard.i18n.model.i18n.persistence.*;
import be.sgerard.i18n.model.workspace.WorkspaceEntity;
import be.sgerard.i18n.model.workspace.WorkspaceEntity_;
import be.sgerard.i18n.service.security.auth.AuthenticationManager;

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
    private final AuthenticationManager authenticationManager;

    public BundleKeyTranslationRepositoryImpl(EntityManager entityManager,
                                              AuthenticationManager authenticationManager) {
        this.entityManager = entityManager;
        this.authenticationManager = authenticationManager;
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

        if (!request.getLocales().isEmpty()) {
            whereClause.getExpressions().add(
                    selectEntry.get(BundleKeyTranslationEntity_.locale).in(request.getLocales().stream().map(Locale::toString).toArray())
            );
        }

        switch (request.getCriterion()) {
            case MISSING_TRANSLATIONS:
                whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.originalValue).isNull());
                whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNull());
                break;
            case UPDATED_TRANSLATIONS:
                whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNotNull());
                break;
            case TRANSLATIONS_CURRENT_USER_UPDATED:
                whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.updatedValue).isNotNull());
                whereClause.getExpressions().add(selectEntry.get(BundleKeyTranslationEntity_.lastEditor).in(authenticationManager.getCurrentUserOrFail().getUser().getId()));
                break;

        }

        request.getKeyPattern()
                .ifPresent(keyPattern ->
                        whereClause.getExpressions().add(criteriaBuilder.like(bundleKeyJoin.get(BundleKeyEntity_.key), keyPattern))
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
