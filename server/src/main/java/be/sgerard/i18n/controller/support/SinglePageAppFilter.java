package be.sgerard.i18n.controller.support;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * {@link WebFilter Web filter} for the single page application.
 *
 * @author Sebastien Gerard
 */
@Component
public class SinglePageAppFilter implements WebFilter {

    /**
     * All prefixes that are not referencing the single page application.
     */
    public static final List<String> NON_STATIC_PATHS = asList("/api", "/auth", "/ws", "/swagger-ui.html");

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain) {
        final String path = exchange.getRequest().getURI().getPath();

        if (path.contains(".")) {
            return chain.filter(exchange);
        } else if (NON_STATIC_PATHS.stream().anyMatch(path::startsWith)) {
            return chain.filter(exchange);
        } else {
            return chain.filter(exchange.mutate().request(exchange.getRequest().mutate().path("/index.html").build()).build());
        }
    }
}
