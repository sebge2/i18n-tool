package be.sgerard.i18n.service.github.external;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Base DTO for all supported GitHub Web-hook events.
 *
 * @author Sebastien Gerard
 */
public abstract class BaseGitHubWebHookEventDto {

    private final Repository repository;

    protected BaseGitHubWebHookEventDto(Repository repository) {
        this.repository = repository;
    }

    /**
     * Returns the {@link Repository repository} involved in this event.
     */
    public Repository getRepository() {
        return repository;
    }

    /**
     * Information about the current repository.
     *
     * @author Sebastien Gerard
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {

        private final String id;
        private final String fullName;

        @JsonCreator
        public Repository(@JsonProperty("id") String id,
                          @JsonProperty("full_name") String fullName) {
            this.id = id;
            this.fullName = fullName;
        }

        /**
         * Returns the unique id of this repository.
         */
        public String getId() {
            return id;
        }

        /**
         * Returns the full name of this repository (ex: sebge2/i18n-tool).
         */
        public String getFullName() {
            return fullName;
        }
    }
}
