import {BehaviorSubject, Observable, Subject} from "rxjs";
import {TranslationsSearchRequest} from "./translations-search-request.model";
import {shareReplay, skip} from "rxjs/operators";
import {TranslationsPage} from "./translations-page.model";

export class TranslationsTableState {

    private readonly _searchRequest$ = new BehaviorSubject<TranslationsSearchRequest>(null);
    private readonly _searchRequestObs$ = this._searchRequest$.pipe(skip(1), shareReplay(1));

    private readonly _unsavedChanges$ = new BehaviorSubject<boolean>(false);
    private readonly _loading$ = new BehaviorSubject<boolean>(false);
    private readonly _saving$ = new BehaviorSubject<boolean>(false);
    private readonly _previousPage$ = new Subject<TranslationsSearchRequest>();
    private readonly _nextPage$ = new Subject<TranslationsSearchRequest>();

    private readonly _page$ = new BehaviorSubject<TranslationsPage>(null);
    private readonly _pageObs$ = this._page$.pipe(skip(1), shareReplay(1));

    constructor() {
    }

    public get searchRequest(): Observable<TranslationsSearchRequest> {
        return this._searchRequestObs$;
    }

    public get searchRequestSync(): TranslationsSearchRequest | undefined {
        return this._searchRequest$.getValue();
    }

    public updateSearchRequest(request: TranslationsSearchRequest): TranslationsTableState {
        this._searchRequest$.next(request);
        return this;
    }

    public get unsavedChanges(): Observable<boolean> {
        return this._unsavedChanges$;
    }

    public notifyUnsavedChanges(unsavedChanges: boolean) {
        this._unsavedChanges$.next(unsavedChanges);
    }

    public get loading(): Observable<boolean> {
        return this._loading$;
    }

    public notifyLoading(loading: boolean) {
        this._loading$.next(loading);
    }

    public get saving(): Observable<boolean> {
        return this._saving$;
    }

    public notifySaving(saving: boolean) {
        this._saving$.next(saving);
    }

    public get page(): Observable<TranslationsPage> {
        return this._pageObs$;
    }

    public get pageSync(): TranslationsPage | undefined {
        return this._page$.getValue();
    }

    public updatePage(page: TranslationsPage) {
        this._page$.next(page);
    }

    public goOnPreviousPage() {
        if (this.searchRequestSync) {
            this._previousPage$.next(this.searchRequestSync.goToPreviousPage(this.pageSync.firstPageKey));
        }
    }

    public previousPageRequest(): Observable<TranslationsSearchRequest> {
        return this._previousPage$;
    }

    public goOnNextPage() {
        if (this.searchRequestSync) {
            this._nextPage$.next(this.searchRequestSync.goToNextPage(this.pageSync.lastPageKey));
        }
    }

    public nextPageRequest(): Observable<TranslationsSearchRequest> {
        return this._nextPage$;
    }
}
