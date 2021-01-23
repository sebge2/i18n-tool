package be.sgerard.i18n.repository.scheduler;

import be.sgerard.i18n.model.scheduler.ScheduledTaskDefinitionSearchRequest;
import be.sgerard.i18n.model.scheduler.persistence.ScheduledTaskDefinitionEntity;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import static be.sgerard.i18n.repository.scheduler.ScheduledTaskDefinitionRepository.*;

/**
 * Implementation of the {@link ScheduledTaskDefinitionRepositoryCustom task definition repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class ScheduledTaskDefinitionRepositoryImpl implements ScheduledTaskDefinitionRepositoryCustom {

    private final ReactiveMongoTemplate template;

    public ScheduledTaskDefinitionRepositoryImpl(ReactiveMongoTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<ScheduledTaskDefinitionEntity> find(ScheduledTaskDefinitionSearchRequest request) {
        return template.find(createQuery(request), ScheduledTaskDefinitionEntity.class);
    }

    /**
     * Creates the {@link Query query} for the specified {@link ScheduledTaskDefinitionSearchRequest request}.
     */
    private Query createQuery(ScheduledTaskDefinitionSearchRequest request) {
        final Query query = new Query();

        request.isEnabled()
                .ifPresent(active -> query.addCriteria(Criteria.where(FIELD_ENABLED).in(active)));

        request.getExecutedAfterOrEqualThan()
                .ifPresent(minimum -> query.addCriteria(Criteria.where(FIELD_LAST_EXECUTION_TIME).gt(minimum)));

        request.getExecutedBeforeOrEqualThan()
                .ifPresent(maximum -> query.addCriteria(Criteria.where(FIELD_LAST_EXECUTION_TIME).lt(maximum)));

        final Sort sort = Sort.by(FIELD_ID).ascending();

        return query.with(sort);
    }
}
