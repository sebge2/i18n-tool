import {BehaviorSubject, Observable} from "rxjs";
import {map, shareReplay, skip} from "rxjs/operators";

export function synchronizedCollection<I, O>(originalCollection: Observable<I[]>,
                                             created: Observable<I>,
                                             updated: Observable<I>,
                                             deleted: Observable<I>,
                                             mapper: ((I) => O),
                                             matcher: ((first: O, second: O) => boolean)): Observable<O[]> {
    const collection: BehaviorSubject<O[]> = new BehaviorSubject<O[]>([])

    originalCollection
        .pipe(map((values: I[]) => values.map(value => mapper(value))))
        .subscribe(values => collection.next(values), error => collection.error(error));

    created
        .pipe(map(value => mapper(value)))
        .subscribe((value: O) => {
            const copy = collection.getValue().slice();

            const index = copy.findIndex(current => matcher(current, value));
            if (index >= 0) {
                copy[index] = value;
            } else {
                copy.push(value);
            }

            collection.next(copy);
        });

    updated
        .pipe(map(value => mapper(value)))
        .subscribe((value: O) => {
            const copy = collection.getValue().slice();

            const index = copy.findIndex(current => matcher(current, value));
            if (index >= 0) {
                copy[index] = value;
            } else {
                copy.push(value);
            }

            collection.next(copy);
        });

    deleted
        .pipe(map(value => mapper(value)))
        .subscribe((value: O) => {
            const copy = collection.getValue().slice();

            const index = copy.findIndex(current => matcher(current, value));
            if (index >= 0) {
                copy.splice(index, 1);
            }

            collection.next(copy);
        });

    return collection;
}

export function synchronizedObject<I, O>(original: Observable<I>,
                                         updated: Observable<I>,
                                         deleted: Observable<I>,
                                         mapper: ((I) => O)): Observable<O> {
    const subject = new BehaviorSubject<O>(null);
    const observable = subject.pipe(skip(1), shareReplay(1));

    original
        .pipe(map((value: I) => mapper(value)))
        .subscribe(value => subject.next(value), error => subject.error(error));

    updated
        .pipe(map(value => mapper(value)))
        .subscribe((value: O) => subject.next(value), error => subject.error(error));

    deleted
        .pipe(map(value => mapper(value)))
        .subscribe((_: O) => subject.next(null), error => subject.error(error));

    return observable;
}
