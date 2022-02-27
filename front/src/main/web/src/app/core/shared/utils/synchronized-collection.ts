import { BehaviorSubject, merge, Observable, Subject } from 'rxjs';
import { map, mergeMap, takeUntil } from 'rxjs/operators';

export class SynchronizedCollection<I, O> {
  private readonly _collection$: BehaviorSubject<O[]> = new BehaviorSubject<O[]>([]);

  private readonly _reloadCollection$ = new BehaviorSubject<void>(undefined);
  private readonly _manualAdd$ = new Subject<O>();
  private readonly _manualUpdate$ = new Subject<O>();
  private readonly _manualDelete$ = new Subject<O>();
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    collectionProvider: () => Observable<I[]>,
    created: Observable<I>,
    updated: Observable<I>,
    deleted: Observable<I>,
    reconnected: Observable<void>,
    mapper: (I) => O,
    matcher: (first: O, second: O) => boolean
  ) {
    this._reloadCollection$
      .pipe(
        takeUntil(this._destroyed$),
        mergeMap(() => collectionProvider()),
        map((values: I[]) => values.map((value) => this.map(value, mapper)))
      )
      .subscribe(
        (values) => this._collection$.next(values),
        (error) => this._collection$.error(error)
      );

    reconnected.pipe(takeUntil(this._destroyed$)).subscribe(() => this.reload());

    merge(this._manualAdd$, created.pipe(map((value) => this.map(value, mapper))))
      .pipe(takeUntil(this._destroyed$))
      .subscribe((value: O) => {
        const copy = this._collection$.getValue().slice();

        const index = copy.findIndex((current) => matcher(current, value));
        if (index >= 0) {
          copy[index] = value;
        } else {
          copy.push(value);
        }

        this._collection$.next(copy);
      });

    merge(this._manualUpdate$, updated.pipe(map((value) => this.map(value, mapper))))
      .pipe(takeUntil(this._destroyed$))
      .subscribe((value: O) => {
        const copy = this._collection$.getValue().slice();

        const index = copy.findIndex((current) => matcher(current, value));
        if (index >= 0) {
          copy[index] = value;
        } else {
          copy.push(value);
        }

        this._collection$.next(copy);
      });

    merge(this._manualDelete$, deleted.pipe(map((value) => this.map(value, mapper))))
      .pipe(takeUntil(this._destroyed$))
      .subscribe((value: O) => {
        const copy = this._collection$.getValue().slice();

        const index = copy.findIndex((current) => matcher(current, value));
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
    this._destroyed$.next(null);
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
    return value !== undefined ? mapper(value) : undefined;
  }
}
