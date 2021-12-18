import { BehaviorSubject, merge, Observable, Subject } from 'rxjs';
import { TranslationsSearchRequest } from './translations-search-request.model';
import { map, shareReplay, skip } from 'rxjs/operators';
import { TranslationsPage } from './translations-page.model';

export interface EnrichedTranslationsSearchRequest {
  request: TranslationsSearchRequest;
  origin: 'NEW' | 'NEXT' | 'PREVIOUS';
}

export class TranslationsTableState {
  private readonly _newSearchRequest$ = new BehaviorSubject<TranslationsSearchRequest>(null);
  private readonly _newSearchRequestObs$ = this._newSearchRequest$.pipe(skip(1), shareReplay(1));
  private readonly _previousPage$ = new Subject<TranslationsSearchRequest>();
  private readonly _nextPage$ = new Subject<TranslationsSearchRequest>();
  private readonly _searchRequest$: Observable<EnrichedTranslationsSearchRequest>;

  private readonly _unsavedChanges$ = new BehaviorSubject<boolean>(false);
  private readonly _loading$ = new BehaviorSubject<boolean>(false);
  private readonly _saving$ = new BehaviorSubject<boolean>(false);

  private readonly _page$ = new BehaviorSubject<TranslationsPage>(null);
  private readonly _pageObs$ = this._page$.pipe(skip(1), shareReplay(1));

  constructor() {
    this._searchRequest$ = merge(
      this.newSearchRequest.pipe(
        map(
          (request) =>
            <EnrichedTranslationsSearchRequest>{
              request: request,
              origin: 'NEW',
            }
        )
      ),
      this.previousPageRequest().pipe(
        map(
          (request) =>
            <EnrichedTranslationsSearchRequest>{
              request: request,
              origin: 'PREVIOUS',
            }
        )
      ),
      this.nextPageRequest().pipe(
        map(
          (request) =>
            <EnrichedTranslationsSearchRequest>{
              request: request,
              origin: 'NEXT',
            }
        )
      )
    ).pipe(shareReplay(1));
  }

  get searchRequest(): Observable<EnrichedTranslationsSearchRequest> {
    return this._searchRequest$;
  }

  get newSearchRequest(): Observable<TranslationsSearchRequest> {
    return this._newSearchRequestObs$;
  }

  // NICE should be based on the other one
  get newSearchRequestSync(): TranslationsSearchRequest | undefined {
    return this._newSearchRequest$.getValue();
  }

  notifyNewSearchRequest(request: TranslationsSearchRequest): TranslationsTableState {
    this._newSearchRequest$.next(request);
    return this;
  }

  get unsavedChanges(): Observable<boolean> {
    return this._unsavedChanges$;
  }

  notifyUnsavedChanges(unsavedChanges: boolean) {
    this._unsavedChanges$.next(unsavedChanges);
  }

  get loading(): Observable<boolean> {
    return this._loading$;
  }

  notifyLoading(loading: boolean) {
    this._loading$.next(loading);
  }

  get saving(): Observable<boolean> {
    return this._saving$;
  }

  notifySaving(saving: boolean) {
    this._saving$.next(saving);
  }

  get page(): Observable<TranslationsPage> {
    return this._pageObs$;
  }

  get pageSync(): TranslationsPage | undefined {
    return this._page$.getValue();
  }

  updatePage(page: TranslationsPage) {
    this._page$.next(page);
  }

  goOnPreviousPage() {
    if (this.newSearchRequestSync) {
      this._previousPage$.next(this.newSearchRequestSync.goToPreviousPage(this.pageSync.firstPageKey));
    }
  }

  previousPageRequest(): Observable<TranslationsSearchRequest> {
    return this._previousPage$;
  }

  goOnNextPage() {
    if (this.newSearchRequestSync) {
      this._nextPage$.next(this.newSearchRequestSync.goToNextPage(this.pageSync.lastPageKey));
    }
  }

  nextPageRequest(): Observable<TranslationsSearchRequest> {
    return this._nextPage$;
  }
}
