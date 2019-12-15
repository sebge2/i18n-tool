package be.sgerard.i18n.support;

import org.apache.commons.lang3.tuple.Pair;
import reactor.core.publisher.Flux;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

/**
 * Bunch of utility method for reactive programming.
 *
 * @author Sebastien Gerard
 */
public final class ReactiveUtils {

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
                        if ((buf.get(0).getLeft() != null) && Objects.equals(buf.get(0).getLeft(), buf.get(1).getRight())) {
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
}
