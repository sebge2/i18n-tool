package be.sgerard.i18n.migration.persistence;

import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.driver.mongodb.springdata.v3.decorator.impl.MongockTemplate;
import com.mongodb.DBRef;
import com.mongodb.client.MongoCollection;
import io.changock.migration.api.annotations.ChangeSet;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static be.sgerard.i18n.model.security.session.persistence.UserLiveSessionEntity.USER_LIVE_SESSION_DOCUMENT;
import static be.sgerard.i18n.model.user.persistence.UserEntity.USER_DOCUMENT;
import static com.mongodb.client.model.Filters.eq;
import static java.util.stream.Collectors.toList;

/**
 * Migration script introduced in 0.0.9.
 */
@ChangeLog
@Slf4j
public class DbRefChangeLog {

    public static final String PREFERENCES_FIELD = "preferences";

    public static final String PREFERRED_LOCALES_FIELD = "preferredLocales";

    /**
     * Migrate all DB-ref contained in user preferences.
     */
    @ChangeSet(id = "userPreferredLocalesDBRefToIds", order = "001", author = "Sebastien Gerard")
    @SuppressWarnings("unused")
    public void migrateUserPreferencesDbRef(MongockTemplate mongockTemplate) {
        final MongoCollection<Document> users = mongockTemplate.getCollection(USER_DOCUMENT);

        for (Document userDocument : users.find()) {
            final Document preferences = getUserPreferences(userDocument);
            final List<Object> userLocales = getUserLocales(preferences);
            final List<String> updatedUserLocales = userLocales
                    .stream()
                    .map(locale ->
                            (locale instanceof DBRef)
                                    ? ((DBRef) locale).getId()
                                    : locale
                    )
                    .map(String.class::cast)
                    .collect(toList());

            if (!Objects.equals(updatedUserLocales, userLocales)) {
                log.info("Migrating user preferred locales of user [{}].", userDocument.get("_id"));

                preferences.put(PREFERRED_LOCALES_FIELD, updatedUserLocales);

                users.replaceOne(
                        eq("_id", userDocument.getString("_id")),
                        userDocument
                );
            } else {
                log.debug("Migration already applied for user preferred locales of user [{}].", userDocument.get("_id"));
            }
        }
    }

    /**
     * Removes all user sessions that still contain DB-ref.
     */
    @ChangeSet(id = "deleteUserSessionsWithDBRf", order = "002", author = "Sebastien Gerard", runAlways = true)
    @SuppressWarnings("unused")
    public void removeUserSessions(MongockTemplate mongockTemplate) {
        final MongoCollection<Document> sessions = mongockTemplate.getCollection(USER_LIVE_SESSION_DOCUMENT);

        for (Document sessionDocument : sessions.find()) {
            final Object userField = sessionDocument.get("user");

            if (userField instanceof DBRef) {
                log.info("Deleting user session with id [{}].", sessionDocument.get("_id"));

                sessions.deleteOne(eq("_id", sessionDocument.getString("_id")));
            }
        }
    }

    /**
     * Returns user's preferences.
     */
    private Document getUserPreferences(Document userDocument) {
        return Optional
                .ofNullable(userDocument.get(PREFERENCES_FIELD))
                .filter(Document.class::isInstance)
                .map(Document.class::cast)
                .orElse(new Document());
    }

    /**
     * Returns user's preferred locales.
     */
    @SuppressWarnings("unchecked")
    private List<Object> getUserLocales(Document preferences) {
        return Optional
                .ofNullable(preferences.get(PREFERRED_LOCALES_FIELD))
                .filter(List.class::isInstance)
                .map(locales -> (List<Object>) locales)
                .orElseGet(Collections::emptyList);
    }
}
