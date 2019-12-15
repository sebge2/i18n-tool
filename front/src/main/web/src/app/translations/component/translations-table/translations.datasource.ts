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

export class CurrentPage {

    public readonly complete: boolean;

    constructor(public index: number, public lastPageKey: string, public pageSize: number) {
        this.complete = (pageSize > 0) && (pageSize % TranslationsDataSource.PAGE_SIZE == 0);
    }
}

export class TranslationsDataSource extends DataSource<FormGroup> {

    public static readonly PAGE_SIZE: number = 100;

    public readonly form: FormArray;
    public loading: boolean = false;
    public totalTranslations: number = 0;

    private readonly _dataStream = new BehaviorSubject<FormGroup[]>([]);

    private _searchRequest: TranslationsSearchRequest = null;

    private _currentPage: CurrentPage;

    private _nextPage = new BehaviorSubject<number>(0);
    private _destroyed$ = new Subject<void>();

    constructor(private _translationService: TranslationService,
                private _notificationService: NotificationService,
                private _formBuilder: FormBuilder) {
        super();

        this.form = _formBuilder.array([]);

        this._nextPage
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => {
                if (this._searchRequest) {
                    this.loading = true;

                    this._translationService
                        .searchTranslations(this._searchRequest, TranslationsDataSource.PAGE_SIZE, _.get(this._currentPage, 'lastPageKey'))
                        .toPromise()
                        .then((page: TranslationsPage) => {
                            this.updateSource(page);

                            if (!this._currentPage.complete) {
                                console.log(`Total translations: ${this.totalTranslations}.`);
                            }
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
                if ((range.end >= this._dataStream.value.length) &&
                    ((!this._currentPage || this._currentPage.complete))
                    && (this._nextPage.value != this.nextPageIndex)) {
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
        this._currentPage = null;
        this.totalTranslations = 0;
        // TODO don't reload everything if needed + edition status

        this.form.clear();
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

    public getBundleKeyId(row: FormGroup): string {
        return row.controls['bundleKeyId'].value;
    }

    public getTranslationValue(translationForm: FormGroup): string {
        return translationForm.controls['value'].value;
    }

    private get nextPageIndex(): number {
        return _.get(this._currentPage, 'index', -1) + 1;
    }

    private loadNextPage() {
        this._nextPage.next(this.nextPageIndex);
    }

    private updateSource(page: TranslationsPage) {
        if (!page) {
            this.form.clear();
            this._dataStream.next([]);
            this._currentPage = null;

            return;
        }

        for (const pageRow of page.rows) {
            const currentRow = this.createBundleKeyRow(pageRow);

            if (_.some(this.form.controls)) {
                const lastRow = <FormGroup>_.last(this.form.controls);

                if (!_.isEqual(this.getWorkspace(lastRow), this.getWorkspace(currentRow))) {
                    this.form.push(this.createWorkspaceRow(pageRow));
                }

                if (!_.isEqual(this.getBundleFile(lastRow), this.getBundleFile(currentRow))) {
                    this.form.push(this.createBundleFileRow(pageRow));
                }
            } else {
                this.form.push(this.createWorkspaceRow(pageRow));
                this.form.push(this.createBundleFileRow(pageRow));
            }

            this.form.push(currentRow);
        }

        this._currentPage = new CurrentPage(this.nextPageIndex, page.lastPageKey, page.rows.length);
        this.totalTranslations += page.rows.length;
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
            bundleKeyId: this._formBuilder.control(pageRow.bundleKeyId),
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
