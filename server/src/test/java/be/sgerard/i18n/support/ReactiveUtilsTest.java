package be.sgerard.i18n.support;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class ReactiveUtilsTest {

    @Test
    public void combine() {
        final Flux<Integer> left = Flux.just(0, 2, 3, 6);
        final Flux<Integer> right = Flux.just(1, 2, 3, 4, 5, 6);

        final List<Pair<Integer, Integer>> actual = ReactiveUtils.combine(left, right, Integer::compareTo)
                .collectList().block();

        assertThat(actual).containsExactly(
                Pair.of(0, null),
                Pair.of(null, 1),
                Pair.of(2, 2),
                Pair.of(3, 3),
                Pair.of(null, 4),
                Pair.of(null, 5),
                Pair.of(6, 6)
        );
    }

}
