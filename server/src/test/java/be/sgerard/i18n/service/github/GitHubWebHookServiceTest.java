package be.sgerard.i18n.service.github;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.io.IOException;

/**
 * @author Sebastien Gerard
 */
public class GitHubWebHookServiceTest {

    @Test
    public void x() throws IOException {
        Mono.fromSupplier(() -> {
            System.out.println("called");

            return "xx";
        })
                .then(Mono.just("x")).subscribe(System.out::println);
    }

}
