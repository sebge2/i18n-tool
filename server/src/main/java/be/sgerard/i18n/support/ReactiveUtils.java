package be.sgerard.i18n.support;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Bunch of utility method for reactive programming.
 *
 * @author Sebastien Gerard
 */
public final class ReactiveUtils {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveUtils.class);

    private ReactiveUtils() {
    }

    /**
     * Combines both flows and returns {@link Pair pairs} with matching and non matching values.
     */
    public static <A, B> Flux<Pair<A, B>> combine(Flux<A> left, Flux<B> right, BiFunction<A, B, Integer> comparator) {
        final AtomicBoolean skip = new AtomicBoolean(false);

        return left
                .map(leftValue -> Pair.of(leftValue, (B) null))
                .mergeOrderedWith(right.map(rightValue -> Pair.of(null, rightValue)), (first, second) -> comparator.apply(first.getLeft(), second.getRight()))
                .buffer(2, 1)
                .flatMapIterable(buf -> {
                    if (skip.get()) {
                        skip.set(false);
                        return emptyList();
                    }
                    if (buf.size() == 2) {
                        if ((buf.get(0).getLeft() != null) && (buf.get(1).getRight() != null) && (comparator.apply(buf.get(0).getLeft(), buf.get(1).getRight()) == 0)) {
                            skip.set(true);

                            return singletonList(
                                    Pair.of(buf.get(0).getLeft(), buf.get(1).getRight())
                            );
                        }

                        return buf.subList(0, 1);
                    }
                    return buf;
                });
    }

    /**
     * Streams objects from an input flux to a Json file.
     */
    public static <I, O> Flux<O> streamObjectToJsonFile(Supplier<Flux<I>> inputFlux,
                                                        Function<I, Mono<O>> mapper,
                                                        ObjectMapper objectMapper,
                                                        File outputFile) {
        return Flux
                .using(
                        () -> {
                            final ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

                            return objectWriter.writeValues(outputFile);
                        },
                        (SequenceWriter sequenceWriter) ->
                                inputFlux
                                        .get()
                                        .flatMap(mapper)
                                        .flatMap(object -> {
                                            try {
                                                sequenceWriter.write(object);

                                                return Mono.just(object);
                                            } catch (IOException e) {
                                                return Mono.error(e);
                                            }
                                        }),
                        (SequenceWriter sequenceWriter) -> {
                            try {
                                sequenceWriter.close();
                            } catch (IOException e) {
                                logger.warn("Error while closing writer.", e);
                            }
                        }
                );
    }

    /**
     * Streams objects from a Json file.
     */
    public static <I, O> Flux<O> streamObjectFromJsonFile(Function<I, Mono<O>> mapper,
                                                          Class<I> inputType,
                                                          ObjectMapper objectMapper,
                                                          File inputFile) {
        return Flux
                .using(
                        () -> {
                            final JsonFactory objectReader = objectMapper.getFactory();

                            return objectReader.createParser(inputFile);
                        },
                        (JsonParser jsonParser) ->
                                Flux
                                        .generate((Consumer<SynchronousSink<I>>) sink -> {
                                            try {
                                                if (jsonParser.nextToken() != null) {
                                                    sink.next(objectMapper.readValue(jsonParser, inputType));
                                                } else {
                                                    sink.complete();
                                                }
                                            } catch (IOException e) {
                                                sink.error(e);
                                            }
                                        })
                                        .flatMap(mapper),
                        (JsonParser jsonParser) -> {
                            try {
                                jsonParser.close();
                            } catch (IOException e) {
                                logger.warn("Error while closing parser.", e);
                            }
                        }
                );
    }
}
