package be.sgerard.i18n.repository.dictionary;

import be.sgerard.i18n.model.dictionary.DictionaryEntrySearchRequest;
import be.sgerard.i18n.model.dictionary.persistence.DictionaryEntryEntity;
import org.bson.BsonRegularExpression;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Implementation of the {@link DictionaryEntryRepositoryCustom dictionary entry repository}.
 *
 * @author Sebastien Gerard
 */
@SuppressWarnings("unused")
public class DictionaryEntryRepositoryImpl implements DictionaryEntryRepositoryCustom {

    private final ReactiveMongoTemplate template;

    public DictionaryEntryRepositoryImpl(ReactiveMongoTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<DictionaryEntryEntity> find(DictionaryEntrySearchRequest request) {
        final Query query = new Query();

        request.getText()
                .filter(textRestriction -> !isEmpty(textRestriction.getText()))
                .ifPresent(textRestriction ->
                        query.addCriteria(
                                Criteria.where(DictionaryEntryRepositoryCustom.fieldTranslation(textRestriction.getLocaleId())).regex(
                                        new BsonRegularExpression(textRestriction.getText(), "i")
                                )
                        )
                );

        request.getSort().ifPresent(sortConfig -> {
            final Sort sort = Sort.by(DictionaryEntryRepositoryCustom.fieldTranslation(sortConfig.getLocaleId()));

            if (sortConfig.isAscending()) {
                query.with(sort.ascending());
            } else {
                query.with(sort.descending());
            }
        });

        return template.find(query, DictionaryEntryEntity.class);
    }
}
