package be.sgerard.i18n.service.repository.github.webhook;

import be.sgerard.i18n.model.repository.github.external.BaseGitHubWebHookEventDto;
import be.sgerard.i18n.model.repository.github.external.GitHubEventType;
import be.sgerard.i18n.model.repository.persistence.GitHubRepositoryEntity;
import be.sgerard.i18n.service.BadRequestException;
import be.sgerard.i18n.service.UnauthorizedRequestException;
import be.sgerard.i18n.service.repository.RepositoryManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.MessageDigest;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.USER_AGENT;
import static org.springframework.util.StringUtils.hasText;

/**
 * Service providing a GitHub Web-hook.
 *
 * <a url="https://developer.github.com/webhooks/">GitHub Doc</a>
 *
 * @author Sebastien Gerard
 */
@Service
public class GitHubWebHookService {

    /**
     * Header name containing the payload signature.
     */
    public static final String SIGNATURE = "X-Hub-Signature";

    /**
     * Header name containing the GitHub user-agent.
     */
    public static final String USER_AGENT_PREFIX = "GitHub-Hookshot/";

    /**
     * Header name containing the payload type.
     */
    public static final String EVENT_TYPE = "X-GitHub-Event";

    private static final Logger logger = LoggerFactory.getLogger(GitHubWebHookService.class);

    private final RepositoryManager repositoryManager;
    private final ObjectMapper objectMapper;
    private final GitHubWebHookEventHandler<BaseGitHubWebHookEventDto> eventHandler;

    public GitHubWebHookService(RepositoryManager repositoryManager,
                                ObjectMapper objectMapper,
                                GitHubWebHookEventHandler<BaseGitHubWebHookEventDto> eventHandler) {
        this.repositoryManager = repositoryManager;
        this.objectMapper = objectMapper;
        this.eventHandler = eventHandler;
    }

    /**
     * Executes the specified request invoked on the Web-hook and returns the result.
     */
    public Mono<String> executeWebHook(RequestEntity<String> requestEntity) {
        checkUserAgent(requestEntity);

        final String rawEventType = requestEntity.getHeaders().getFirst(EVENT_TYPE);
        final Optional<GitHubEventType> eventType = getEventType(rawEventType);

        if (eventType.isEmpty()) {
            logger.debug("Ignore GitHub event type [" + eventType + "].");

            return Mono.just("Unsupported event type [" + rawEventType + "]. Ignoring it.");
        }

        final BaseGitHubWebHookEventDto event = readEvent(requestEntity, eventType.get());

        return repositoryManager
                .findAll()
                .filter(GitHubRepositoryEntity.class::isInstance)
                .map(GitHubRepositoryEntity.class::cast)
                .filter(repository -> Objects.equals(repository.getName(), event.getRepository().getFullName()))
                .flatMap(repository -> {
                    final String signature = getSignature(requestEntity);

                    if (!isPayloadSignatureValid(requestEntity.getBody(), signature, repository.getWebHookSecret().orElse(null))) {
                        return Mono.error(UnauthorizedRequestException.invalidSignatureException(signature));
                    }

                    return eventHandler.handle(repository, event);
                })
                .then(Mono.just("Signature Verified.\n" + "Received " + ((requestEntity.getBody() != null) ? requestEntity.getBody().getBytes().length : 0) + " bytes."));
    }

    /**
     * Returns the event type from the specified raw event type. If the event type is not supported,
     * nothing is returned.
     */
    private Optional<GitHubEventType> getEventType(String rawEventType) {
        if (!hasText(rawEventType)) {
            throw BadRequestException.missingHeader(EVENT_TYPE);
        }

        return GitHubEventType.findType(rawEventType);
    }

    /**
     * Returns the signature from the specified {@link RequestEntity request} (can be <tt>null</tt>).
     */
    private String getSignature(RequestEntity<String> requestEntity) {
        final String signature = requestEntity.getHeaders().getFirst(SIGNATURE);

        return !hasText(signature) ? null : signature;
    }

    /**
     * Checks the user-agent of the specified repository.
     */
    private void checkUserAgent(RequestEntity<String> requestEntity) {
        final String userAgent = requestEntity.getHeaders().getFirst(USER_AGENT);

        if (!hasText(userAgent) || !userAgent.startsWith(USER_AGENT_PREFIX)) {
            throw BadRequestException.missingHeader(USER_AGENT);
        }
    }

    /**
     * Returns the {@link BaseGitHubWebHookEventDto event} from the specified request.
     */
    private BaseGitHubWebHookEventDto readEvent(RequestEntity<String> requestEntity, GitHubEventType eventType) {
        try {
            return objectMapper.readValue(requestEntity.getBody(), eventType.getDtoType());
        } catch (JsonProcessingException e) {
            throw BadRequestException.cannotParseException(requestEntity.getBody(), e);
        }
    }

    /**
     * Checks that the signature of the specified payload match the expected secret key (can be <tt>null</tt>) .
     */
    private boolean isPayloadSignatureValid(String payload, String signature, String secretKey) {
        if (secretKey == null) {
            return true;
        }

        if (signature == null) {
            return false;
        }

        return MessageDigest.isEqual(
                signature.getBytes(),
                String.format("sha1=%s", new HmacUtils(HmacAlgorithms.HMAC_SHA_1, secretKey).hmacHex(payload)).getBytes()
        );
    }

}
