import {BehaviorSubject, Observable} from "rxjs";

export class DictionaryTableState {

    private readonly _unsavedChanges$ = new BehaviorSubject<boolean>(false);
    private readonly _loading$ = new BehaviorSubject<boolean>(false);
    private readonly _saving$ = new BehaviorSubject<boolean>(false);

    constructor() {
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
}