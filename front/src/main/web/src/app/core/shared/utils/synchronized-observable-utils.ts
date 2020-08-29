import {BehaviorSubject, Observable} from "rxjs";
import {map, shareReplay, skip} from "rxjs/operators";
import * as _ from "lodash";

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

export function updateOriginalCollection<E>(originalElements: E[], updatedElements: E[], field: keyof E): E[] {
    return updatedElements
        .filter(updatedElement =>
            _.some(originalElements, originalElement =>
                _.isEqual(
                    _.get(originalElement, field),
                    _.get(updatedElement, field)
                )
            )
        );
}
