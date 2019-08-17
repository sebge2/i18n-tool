package be.sgerard.poc.githuboauth.model.git;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

/**
 * @author Sebastien Gerard
 */
@JsonDeserialize(using = GitHubPullRequestEventDto.Deserializer.class)
public class GitHubPullRequestEventDto {

    private final String id;
    private final int number;
    private final PullRequestStatus status;

    public GitHubPullRequestEventDto(String id,
                                     int number,
                                     PullRequestStatus status) {
        this.id = id;
        this.number = number;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public int getNumber() {
        return number;
    }

    public PullRequestStatus getStatus() {
        return status;
    }

    public static final class Deserializer extends JsonDeserializer<GitHubPullRequestEventDto> {

        @Override
        public GitHubPullRequestEventDto deserialize(JsonParser jp, DeserializationContext context) throws IOException {
            final DeserializedEventDto deserializedEventDto = jp.readValueAs(DeserializedEventDto.class);

            return new GitHubPullRequestEventDto(
                deserializedEventDto.getDeserializedPullRequest().getId(),
                deserializedEventDto.getDeserializedPullRequest().getNumber(),
                deserializedEventDto.getDeserializedPullRequest().getState()
            );
        }
    }

    private static final class DeserializedEventDto {

        private DeserializedPullRequest deserializedPullRequest;

        public DeserializedPullRequest getDeserializedPullRequest() {
            return deserializedPullRequest;
        }

        @JsonSetter("pull_request")
        public void setDeserializedPullRequest(DeserializedPullRequest deserializedPullRequest) {
            this.deserializedPullRequest = deserializedPullRequest;
        }

        @JsonAnySetter
        public void setOtherProperty(String key, Object value) {
        }
    }

    private static final class DeserializedPullRequest {

        private String id;
        private int number;
        private PullRequestStatus state;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public PullRequestStatus getState() {
            return state;
        }

        public void setState(PullRequestStatus state) {
            this.state = state;
        }

        @JsonAnySetter
        public void setOtherProperty(String key, Object value) {
        }
    }

}
