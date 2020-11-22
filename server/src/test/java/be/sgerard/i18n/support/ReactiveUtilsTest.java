package be.sgerard.i18n.support;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Sebastien Gerard
 */
public class ReactiveUtilsTest {

    @TempDir
    public File tempDir;

    private final ObjectMapper objectMapper = new ObjectMapper();

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

    @Test
    public void streamObjectToJsonFile() throws IOException {
        final File tempFile = new File(tempDir, "reactive-utils-test.json");

        StepVerifier
                .create(
                        ReactiveUtils
                                .streamObjectToJsonFile(
                                        () -> Flux.just("test1", "test2"),
                                        object -> Mono.just(new SampleDto(object)),
                                        objectMapper,
                                        tempFile
                                )
                )
                .expectNext(new SampleDto("test1"), new SampleDto("test2"))
                .expectComplete()
                .verify();

        assertThat(objectMapper.readValues(objectMapper.getFactory().createParser(tempFile), SampleDto.class).readAll())
                .containsExactly(new SampleDto("test1"), new SampleDto("test2"));
    }

    @Test
    public void streamObjectFromJsonFile() throws IOException {
        final File tempFile = new File(tempDir, "reactive-utils-test.json");
        FileUtils.write(tempFile, objectMapper.writeValueAsString(new SampleDto("test1")));
        FileUtils.write(tempFile, objectMapper.writeValueAsString(new SampleDto("test2")), true);

        StepVerifier
                .create(
                        ReactiveUtils
                                .streamObjectFromJsonFile(
                                        (SampleDto dto) -> Mono.just(dto.value),
                                        SampleDto.class,
                                        objectMapper,
                                        tempFile
                                )
                )
                .expectNext("test1", "test2")
                .expectComplete()
                .verify();
    }

    @Test
    public void streamObjectToJsonFileThenFromJsonFile() {
        final File tempFile = new File(tempDir, "reactive-utils-test.json");

        StepVerifier
                .create(
                        ReactiveUtils
                                .streamObjectToJsonFile(
                                        () -> Flux.just("test1", "test2"),
                                        object -> Mono.just(new SampleDto(object)),
                                        objectMapper,
                                        tempFile
                                )
                )
                .expectNext(new SampleDto("test1"), new SampleDto("test2"))
                .expectComplete()
                .verify();

        StepVerifier
                .create(
                        ReactiveUtils
                                .streamObjectFromJsonFile(
                                        (SampleDto dto) -> Mono.just(dto.value),
                                        SampleDto.class,
                                        objectMapper,
                                        tempFile
                                )
                )
                .expectNext("test1", "test2")
                .expectComplete()
                .verify();
    }

    @Getter
    @EqualsAndHashCode
    private static final class SampleDto {

        private final String value;

        @JsonCreator
        private SampleDto(@JsonProperty("value") String value) {
            this.value = value;
        }
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
