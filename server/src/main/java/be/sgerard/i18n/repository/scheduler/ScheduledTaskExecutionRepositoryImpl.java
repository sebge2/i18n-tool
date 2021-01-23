package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskExecutionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskExecutionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import static be.sgerard.i18n.repository.scheduler.ScheduledTaskExecutionRepository.*;

/**
 * Implementation of the {@link ScheduledTaskExecutionRepositoryCustom task execution repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class ScheduledTaskExecutionRepositoryImpl implements ScheduledTaskExecutionRepositoryCustom {

    private final ReactiveMongoTemplate template;

    public ScheduledTaskExecutionRepositoryImpl(ReactiveMongoTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<ScheduledTaskExecutionEntity> find(ScheduledTaskExecutionSearchRequest request) {
        return template.find(createQuery(request), ScheduledTaskExecutionEntity.class);
    }

    /**
     * Creates the {@link Query query} for the specified {@link ScheduledTaskExecutionSearchRequest request}.
     */
    private Query createQuery(ScheduledTaskExecutionSearchRequest request) {
        final Query query = new Query();

        request.getTaskDefinitionId()
                .ifPresent(taskDefinitionId -> query.addCriteria(Criteria.where(FIELD_DEFINITION_ID).in(taskDefinitionId)));

        query.addCriteria(Criteria.where(FIELD_STATUS).in(request.getStatuses()));

        request.getExecutedAfterOrEqualThan()
                .ifPresent(minimum -> query.addCriteria(Criteria.where(FIELD_START_TIME).gt(minimum)));

        request.getExecutedBeforeOrEqualThan()
                .ifPresent(maximum -> query.addCriteria(Criteria.where(FIELD_START_TIME).lt(maximum)));

        request.getLimit().ifPresent(query::limit);

        final Sort sort = request.isAscending()
                ? Sort.by(FIELD_START_TIME).ascending()
                : Sort.by(FIELD_START_TIME).descending();

        return query.with(sort);
    }
}
