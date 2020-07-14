import {BehaviorSubject, Observable, Subject} from "rxjs";
import {map} from "rxjs/operators";

export function synchronizedCollection<I, O>(originalCollection: Observable<I[]>,
                                             created: Observable<I>,
                                             updated: Observable<I>,
                                             deleted: Observable<I>,
                                             mapper: ((I) => O),
                                             matcher: ((first: O, second: O) => boolean)): Observable<O[]> {
    const collection: BehaviorSubject<O[]> = new BehaviorSubject<O[]>([])

    originalCollection
        .pipe(map((values: I[]) => values.map(value => mapper(value))))
        .toPromise()
        .then(values => collection.next(values))
        .catch(reason => collection.error(reason));

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
    const subject = new Subject<O>();

    original
        .pipe(map((value: I) => mapper(value)))
        .toPromise()
        .then(value => subject.next(value))
        .catch(reason => subject.error(reason));

    updated
        .pipe(map(value => mapper(value)))
        .subscribe((value: O) => subject.next(value));

    deleted
        .pipe(map(value => mapper(value)))
        .subscribe((_: O) => subject.next(null));

    return subject;
}
