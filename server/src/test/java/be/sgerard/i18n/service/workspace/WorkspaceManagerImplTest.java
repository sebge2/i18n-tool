package be.sgerard.i18n.service.workspace;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Arrays.asList;

/**
 * @author Sebastien Gerard
 */
public class WorkspaceManagerImplTest {

    @Test
    public void y() {
        final List<Pair<String, String>> elements = new ArrayList<>();

        Flux.just("first", "second", "fourth")
                .join(
                        Flux.just("first", "second", "third"),
                        t11 -> Flux.never(),
                        t1 -> Flux.never(),
                        (s, s2) -> Pair.of(s, s2)

                )
                .filter(pair -> Objects.equals(pair.getKey(), pair.getValue()))
                .log()
                .subscribe(new Subscriber<Pair<String, String>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Pair<String, String> integer) {
                        elements.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("on complete");
                    }
                });

        System.out.println(elements);

//        final Iterator<Pair<String, String>> iterator = block.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
    }


    @Test
    public void x() {
        final List<Pair<String, String>> elements = new ArrayList<>();

        Flux.just("first", "second", "third")
                .groupJoin(
                        Flux.just("first", "second", "fourth"),
                        t11 -> Flux.never(),
                        t1 -> Flux.never(),
                        (s, flux) -> flux
                                .filter(x -> Objects.equals(x, s))
                                .map(x -> Pair.of(s, x))
                                .defaultIfEmpty(Pair.of(null, s))

                )
                .flatMap(t -> t)
                .log()
                .subscribe(new Subscriber<Pair<String, String>>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        s.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(Pair<String, String> integer) {
                        elements.add(integer);
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.println(t);
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("on complete");
                    }
                });

        System.out.println(elements);

//        final Iterator<Pair<String, String>> iterator = block.iterator();
//        while (iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
    }

    @Test
    public void a() {
        Flux<Pair<Integer, Integer>> a = Flux.just(0, 2, 3, 6).map(val -> Pair.of(val, null));
        Flux<Pair<Integer, Integer>> b = Flux.just(1, 2, 3, 4, 5, 6).map(val -> Pair.of(null, val));

        final AtomicBoolean skip = new AtomicBoolean(false);

        a
                .mergeOrderedWith(b, (o1, o2) -> Comparator.<Integer>naturalOrder().compare(o1.getLeft(), o2.getRight()))
                .buffer(2, 1)
                .flatMapIterable(buf -> {
//                    System.out.println(buf);

                    if (skip.get()) {
                        skip.set(false);
                        return Collections.emptyList();
                    }
                    if (buf.size() == 2) {
                        if ((buf.get(0).getLeft() != null) &&

                                Objects.equals(buf.get(0).getLeft(), buf.get(1).getRight())) {
                            skip.set(true);

                            return Collections.singletonList(
                                    Pair.of(buf.get(0).getLeft(), buf.get(1).getRight())
                            );
                        }

                        return buf.subList(0, 1);
                    }
                    return buf;
                })
//        })
                .subscribe(x -> {
                    System.out.println(x);
                });

    }

    @Test
    public void toto(){
        final Mono<String> test = Mono.just("test")
                .map(x -> {
                    throw new IllegalStateException("");
                });

        final Mono<String> map = test.then(
                Mono.fromSupplier(() -> "hello world")
        );


        System.out.println(map.block());

    }



}
