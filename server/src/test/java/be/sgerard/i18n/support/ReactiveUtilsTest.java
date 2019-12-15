package be.sgerard.i18n.support;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class ReactiveUtilsTest {

    @Test
    public void combine() {
        final Flux<FirstWrapper> left = Flux.just(0, 2, 3, 6).map(FirstWrapper::new);
        final Flux<Integer> right = Flux.just(1, 2, 3, 4, 5, 6);

        final List<Pair<FirstWrapper, Integer>> actual = ReactiveUtils.combine(left, right, (first, second) -> Integer.compare(first.value, second))
                .collectList().block();

        assertThat(actual).containsExactly(
                Pair.of(new FirstWrapper(0), null),
                Pair.of(null, 1),
                Pair.of(new FirstWrapper(2), 2),
                Pair.of(new FirstWrapper(3), 3),
                Pair.of(null, 4),
                Pair.of(null, 5),
                Pair.of(new FirstWrapper(6), 6)
        );
    }

    private static final class FirstWrapper {

        private final int value;

        private FirstWrapper(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            final FirstWrapper that = (FirstWrapper) o;

            return value == that.value;
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }
    }

}
