package be.sgerard.poc.githuboauth.service.git;

import be.sgerard.poc.githuboauth.configuration.AppProperties;
import be.sgerard.poc.githuboauth.model.git.GitHubPullRequestEventDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;
import java.util.Objects;

/**
 * <a url="https://developer.github.com/webhooks/">GitHub Doc</a>
 *
 * @author Sebastien Gerard
 */
@Service
public class GitHubWebHookService {

    private static final Logger logger = LoggerFactory.getLogger(GitHubWebHookService.class);

    public static final String SIGNATURE = "X-Hub-Signature";

    public static final String USER_AGENT_PREFIX = "GitHub-Hookshot/";

    public static final String EVENT_TYPE = "X-GitHub-Event";

    public static final String PULL_REQUEST_EVENT = "pull_request";

    private final ObjectMapper objectMapper;
    private final String secretKey;
    private final List<WebHookCallback> callbacks;

    public GitHubWebHookService(ObjectMapper objectMapper,
                                AppProperties appProperties,
                                List<WebHookCallback> callbacks) {
        this.objectMapper = objectMapper;
        this.secretKey = appProperties.getGitHubWebhookSecret();
        this.callbacks = callbacks;
    }

    public ResponseEntity<?> executeWebHook(RequestEntity<String> requestEntity) throws Exception {
        checkUserAgent(requestEntity);

        final String signature = checkSignature(requestEntity);
        final String eventType = checkEventType(requestEntity);

        if (!isPayloadSignatureValid(requestEntity, signature)) {
            return new ResponseEntity<>("Invalid signature.", HttpStatus.UNAUTHORIZED);
        }

        if (Objects.equals(PULL_REQUEST_EVENT, eventType)) {
            final GitHubPullRequestEventDto event = readPayload(requestEntity);

            for (WebHookCallback callback : callbacks) {
                callback.onPullRequest(event);
            }
        } else {
            logger.info("Ignore GitHub event type [" + eventType + "].");
        }

        return new ResponseEntity<>(
            "Signature Verified.\n" + "Received " + ((requestEntity.getBody() != null) ? requestEntity.getBody().getBytes().length : 0) + " bytes.",
            HttpStatus.OK
        );
    }

    private String checkEventType(RequestEntity<String> requestEntity) {
        final String eventType = requestEntity.getHeaders().getFirst(EVENT_TYPE);
        if (eventType == null) {
            throw new IllegalArgumentException("No event type given.");
        }
        return eventType;
    }

    private String checkSignature(RequestEntity<String> requestEntity) {
        final String signature = requestEntity.getHeaders().getFirst(SIGNATURE);

        if (signature == null) {
            throw new IllegalArgumentException("No signature given.");
        }

        return signature;
    }

    private void checkUserAgent(RequestEntity<String> requestEntity) {
        final String userAgent = requestEntity.getHeaders().getFirst(HttpHeaders.USER_AGENT);

        if (Objects.isNull(userAgent) || !userAgent.startsWith(USER_AGENT_PREFIX)) {
            throw new IllegalArgumentException("Invalid request, the user agent is invalid [" + userAgent + "].");
        }
    }

    private boolean isPayloadSignatureValid(RequestEntity<String> requestEntity, String signature) {
        return MessageDigest.isEqual(signature.getBytes(), String.format("sha1=%s", new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey).hmacHex(requestEntity.getBody())).getBytes());
    }

    private GitHubPullRequestEventDto readPayload(RequestEntity<String> requestEntity) {
        try {
            return objectMapper.readValue(requestEntity.getBody(), GitHubPullRequestEventDto.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Unable to parse response.", e);
        }
    }

}
