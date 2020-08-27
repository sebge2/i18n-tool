import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable, Subject} from "rxjs";
import {TranslationService} from "../../service/translation.service";
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {TranslationsPage} from "../../model/search/translations-page.model";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {TranslationsPageRow} from "../../model/search/translations-page-row.model";
import {TranslationsPageTranslation} from "../../model/search/translations-page-translation.model";
import {NotificationService} from "../../../core/notification/service/notification.service";

export enum RowType {

    WORKSPACE = 'WORKSPACE',

    BUNDLE_FILE = 'BUNDLE_FILE',

    BUNDLE_KEY = 'BUNDLE_KEY'
}

export class TranslationsDataSource extends DataSource<FormGroup> {

    private static readonly PAGE_SIZE: number = 100;

    public loading: boolean = false;
    public readonly form: FormArray;

    private readonly _dataStream = new BehaviorSubject<FormGroup[]>([]);

    private _searchRequest: TranslationsSearchRequest = null;
    private _lastPageKey: string;
    private _nextPage = new Subject<void>();
    private _destroyed$ = new Subject<void>();

    constructor(private _translationService: TranslationService,
                private _notificationService: NotificationService,
                private _formBuilder: FormBuilder) {
        super();

        this.form = _formBuilder.array([]);

        this._nextPage
            .pipe(takeUntil(this._destroyed$))
            .subscribe(_ => {
                if (this._searchRequest) {
                    this.loading = true;

                    this._translationService
                        .searchTranslations(this._searchRequest, TranslationsDataSource.PAGE_SIZE, this._lastPageKey)
                        .toPromise()
                        .then((page: TranslationsPage) => {
                            if (!page.lastPageKey) {
                                // no more result
                            }

                            this.updateSource(page);
                        })
                        .catch(error => {
                            console.error('Error while searching for translations.', error);
                            this._notificationService.displayErrorMessage('TRANSLATIONS.ERROR.SEARCH', error)
                        })
                        .finally(() => this.loading = false)
                } else {
                    this.updateSource(null);
                }
            });
    }

    public connect(collectionViewer: CollectionViewer): Observable<FormGroup[]> {
        collectionViewer
            .viewChange
            .pipe(takeUntil(this._destroyed$))
            .subscribe(range => {
                if ((range.end >= this._dataStream.value.length)
                    && (this._dataStream.value.length % TranslationsDataSource.PAGE_SIZE == 0)
                    && !this.loading) {
                    this.loadNextPage();
                }
            });

        return this._dataStream;
    }

    public disconnect(collectionViewer: CollectionViewer): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public setRequest(request: TranslationsSearchRequest) {
        this._searchRequest = request;
        this._lastPageKey = null;

        this._dataStream.next([]);
        this.loadNextPage();
    }

    public getRowType(row: FormGroup): RowType {
        return row.controls['type'].value;
    }

    public getRowBundleKey(row: FormGroup): string {
        return row.controls['bundleKey'].value;
    }

    public getRowTranslations(row: FormGroup): FormGroup[] {
        return <FormGroup[]>(<FormArray>row.controls['translations']).controls;
    }

    public getWorkspace(row: FormGroup): string {
        return row.controls['workspace'].value;
    }

    public getBundleFile(row: FormGroup): string {
        return row.controls['bundleFile'].value;
    }

    private loadNextPage() {
        this._nextPage.next();
    }

    private updateSource(page: TranslationsPage) {
        if (!page) {
            this.form.clear();
            this._dataStream.next([]);

            return;
        }

        for (const pageRow of page.rows) {
            const currentRow = this.createBundleKeyRow(pageRow);

            if (_.some(this.form.controls)) {
                const lastRow = <FormGroup>_.last(this.form.controls);

                if(!_.isEqual(this.getWorkspace(lastRow), this.getWorkspace(currentRow))){
                    this.form.push(this.createWorkspaceRow(pageRow));
                }

                if(!_.isEqual(this.getBundleFile(lastRow), this.getBundleFile(currentRow))){
                    this.form.push(this.createBundleFileRow(pageRow));
                }
            } else {
                this.form.push(this.createWorkspaceRow(pageRow));
                this.form.push(this.createBundleFileRow(pageRow));
            }

            this.form.push(currentRow);
        }

        this._lastPageKey = page.lastPageKey;
        this._dataStream.next(<FormGroup[]>this.form.controls);
    }

    private createWorkspaceRow(pageRow: TranslationsPageRow): FormGroup {
        return this._formBuilder.group({
            type: this._formBuilder.control(RowType.WORKSPACE),
            workspace: this._formBuilder.control(pageRow.workspace),
            bundleFile: this._formBuilder.control(pageRow.bundleFile)
        });
    }

    private createBundleFileRow(pageRow: TranslationsPageRow): FormGroup {
        return this._formBuilder.group({
            type: this._formBuilder.control(RowType.BUNDLE_FILE),
            workspace: this._formBuilder.control(pageRow.workspace),
            bundleFile: this._formBuilder.control(pageRow.bundleFile)
        });
    }

    private createBundleKeyRow(pageRow: TranslationsPageRow): FormGroup {
        return this._formBuilder.group({
            type: this._formBuilder.control(RowType.BUNDLE_KEY),
            workspace: this._formBuilder.control(pageRow.workspace),
            bundleFile: this._formBuilder.control(pageRow.bundleFile),
            bundleKey: this._formBuilder.control(pageRow.bundleKey),
            translations: this._formBuilder.array(
                _.range(0, this._searchRequest.locales.length).map(i => this.createBundleKeyCell(pageRow.translations[i]))
            )
        });
    }

    private createBundleKeyCell(translationsPageTranslation: TranslationsPageTranslation): FormGroup {
        return this._formBuilder.group({
            value: this._formBuilder.control(
                translationsPageTranslation.updatedValue
                    ? translationsPageTranslation.updatedValue
                    : translationsPageTranslation.originalValue
            ),
            originalValue: this._formBuilder.control(translationsPageTranslation.originalValue)
        });
    }
}
