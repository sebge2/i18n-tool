package be.sgerard.i18n.controller.support;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * {@link WebFilter Filter} placing a unique identifier for every incoming request.
 *
 * @author Sebastien Gerard
 */
@Component
public class RequestIdWebFilter implements WebFilter {

    /**
     * Parameter name containing the unique request id.
     */
    public static final String REQUEST_ID_CONTEXT_PARAM = "REQUEST_ID";

    /**
     * Returns the request id contained in the specified {@link Context context}.
     */
    public static String getRequestIdOrFail(Context context) {
        return getRequestId(context)
                .orElseThrow(() -> new IllegalStateException("There is no request id in the current context."));
    }

    /**
     * Returns the request id contained in the specified {@link Context context}.
     */
    public static Optional<String> getRequestId(Context context) {
        return context
                .getOrEmpty(REQUEST_ID_CONTEXT_PARAM)
                .map(id -> Objects.toString(id, null));
    }

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        return chain.filter(exchange)
                .subscriberContext(context -> context.put(REQUEST_ID_CONTEXT_PARAM, UUID.randomUUID().toString()));
    }
}
