import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {filter, map, mergeMap, shareReplay, skip, startWith, takeUntil} from "rxjs/operators";

export class SynchronizedObject<I, O> {

    private readonly _element$: BehaviorSubject<O> = new BehaviorSubject<O>(undefined);
    private readonly _elementObs$ = this._element$.pipe(skip(1), shareReplay(1));

    private readonly _reloadElement$ = new BehaviorSubject<void>(undefined);
    private readonly _manualUpdate$ = new BehaviorSubject<O>(undefined);
    private readonly _manualDelete$ = new BehaviorSubject<O>(undefined);
    private readonly _destroyed$ = new Subject<void>();

    constructor(elementProvider: (() => Observable<I>),
                updated: Observable<I>,
                deleted: Observable<I>,
                reconnected: Observable<void>,
                mapper: ((I) => O)) {
        this._reloadElement$
            .pipe(
                takeUntil(this._destroyed$),
                mergeMap(() => elementProvider()),
                map((value: I) => mapper(value))
            )
            .subscribe(
                values => this._element$.next(values),
                error => this._element$.error(error)
            );

        reconnected
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => this.reload());

        combineLatest([this._manualUpdate$, updated.pipe(startWith(<O>undefined), map(value => this.map(value, mapper)))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([manualUpdate, streamUpdate]) => SynchronizedObject.keepDefinedValue(manualUpdate, streamUpdate)),
                filter(element => element !== undefined)
            )
            .subscribe((element: O) => this._element$.next(element));

        combineLatest([this._manualDelete$, deleted.pipe(startWith(<I>undefined), map(value => this.map(value, mapper)))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([manualDelete, streamDelete]) =>  SynchronizedObject.keepDefinedValue(manualDelete, streamDelete)),
                filter(element => (element !== undefined))
            )
            .subscribe((_) => this._element$.next(null));
    }

    public get element(): Observable<O> {
        return this._elementObs$;
    }

    public destroy() {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public reload() {
        this._reloadElement$.next();
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
