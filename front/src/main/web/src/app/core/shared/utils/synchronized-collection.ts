import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {filter, map, mergeMap, startWith, takeUntil} from "rxjs/operators";

export class SynchronizedCollection<I, O> {

    private readonly _collection$: BehaviorSubject<O[]> = new BehaviorSubject<O[]>([]);

    private readonly _reloadCollection$ = new BehaviorSubject<void>(undefined);
    private readonly _manualAdd$ = new BehaviorSubject<O>(undefined);
    private readonly _manualUpdate$ = new BehaviorSubject<O>(undefined);
    private readonly _manualDelete$ = new BehaviorSubject<O>(undefined);
    private readonly _destroyed$ = new Subject<void>();

    constructor(collectionProvider: (() => Observable<I[]>),
                created: Observable<I>,
                updated: Observable<I>,
                deleted: Observable<I>,
                reconnected: Observable<void>,
                mapper: ((I) => O),
                matcher: ((first: O, second: O) => boolean)) {
        this._reloadCollection$
            .pipe(
                takeUntil(this._destroyed$),
                mergeMap(() => collectionProvider()),
                map((values: I[]) => values.map(value => this.map(value, mapper)))
            )
            .subscribe(
                values => this._collection$.next(values),
                error => this._collection$.error(error)
            );

        reconnected
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this.reload());

        combineLatest([this._manualAdd$, created.pipe(startWith(<O>undefined), map(value => this.map(value, mapper)))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([manualAdd, streamAdd]) => SynchronizedCollection.keepDefinedValue(manualAdd, streamAdd)),
                filter(element => (element !== undefined))
            )
            .subscribe((value: O) => {
                const copy = this._collection$.getValue().slice();

                const index = copy.findIndex(current => matcher(current, value));
                if (index >= 0) {
                    copy[index] = value;
                } else {
                    copy.push(value);
                }

                this._collection$.next(copy);
            });

        combineLatest([this._manualUpdate$, updated.pipe(startWith(<O>undefined), map(value => this.map(value, mapper)))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([manualUpdate, streamUpdate]) => SynchronizedCollection.keepDefinedValue(manualUpdate, streamUpdate)),
                filter(element => (element !== undefined))
            )
            .subscribe((value: O) => {
                const copy = this._collection$.getValue().slice();

                const index = copy.findIndex(current => matcher(current, value));
                if (index >= 0) {
                    copy[index] = value;
                } else {
                    copy.push(value);
                }

                this._collection$.next(copy);
            });

        combineLatest([this._manualDelete$, deleted.pipe(startWith(<O>undefined), map(value => this.map(value, mapper)))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([manualDelete, streamDelete]) =>  SynchronizedCollection.keepDefinedValue(manualDelete, streamDelete)),
                filter(element => (element !== undefined))
            )
            .subscribe((value: O) => {
                const copy = this._collection$.getValue().slice();

                const index = copy.findIndex(current => matcher(current, value));
                if (index >= 0) {
                    copy.splice(index, 1);
                }

                this._collection$.next(copy);
            });
    }

    public get collection(): Observable<O[]> {
        return this._collection$;
    }

    public destroy() {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public reload() {
        this._reloadCollection$.next();
    }

    public add(element: O) {
        this._manualAdd$.next(element);
    }

    public update(element: O) {
        this._manualUpdate$.next(element);
    }

    public delete(element: O) {
        this._manualDelete$.next(element);
    }

    private map<A>(value: A, mapper: (I) => O) {
        return (value !== undefined) ? mapper(value) : undefined;
    }

    private static keepDefinedValue(firstValue, secondValue) {
        return (firstValue !== undefined) ? firstValue : secondValue;
    }
}
