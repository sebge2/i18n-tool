package be.sgerard.i18n.controller.support;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.codec.ResourceHttpMessageWriter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;

import static java.util.Collections.singletonList;

/**
 * Web error controller.
 *
 * @author Sebastien Gerard
 */
@Component
@Order(-2)
public class ErrorExceptionHandler extends AbstractErrorWebExceptionHandler {

    public ErrorExceptionHandler(ApplicationContext applicationContext) {
        super(new DefaultErrorAttributes(), new WebProperties.Resources(), applicationContext);

        setMessageWriters(singletonList(new ResourceHttpMessageWriter()));
    }

    @Override
    @SuppressWarnings("NullableProblems")
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
        final URI redirectUri = createRedirectUri(request);

        return ServerResponse
                .temporaryRedirect(redirectUri)
                .build();
    }

    private URI createRedirectUri(ServerRequest request)  {
        try {
            return new URIBuilder("/error").addParameter("origin", request.uri().toString()).build();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Error while creating redirect URI.", e);
        }
    }
}
