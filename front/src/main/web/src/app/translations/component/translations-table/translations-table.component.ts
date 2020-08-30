import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/search/translations-search-request.model";
import {RowType, TranslationsDataSource} from "./translations.datasource";
import {TranslationService} from "../../service/translation.service";
import {FormArray, FormBuilder, FormGroup} from "@angular/forms";
import {Observable, Subject} from "rxjs";
import {auditTime, takeUntil} from "rxjs/operators";
import {NotificationService} from "../../../core/notification/service/notification.service";
import {EnrichedWorkspace} from "../../model/workspace/enriched-workspace.model";
import {WorkspaceService} from "../../service/workspace.service";
import {BundleFile} from "../../model/workspace/bundle-file.model";
import * as _ from "lodash";
import {TranslationUpdateDto} from "../../../api";

class DirtyTranslationForm {

    constructor(public bundleKeyId: string,
                public localeId: string,
                public translation: string,
                public translationForm: FormGroup) {
    }
}

@Component({
    selector: 'app-translations-table',
    templateUrl: './translations-table.component.html',
    styleUrls: ['./translations-table.component.css']
})
export class TranslationsTableComponent implements OnInit, OnDestroy {

    public dataSource: TranslationsDataSource;
    public RowType = RowType;

    private _searchRequest: TranslationsSearchRequest;
    private _destroyed$ = new Subject<void>();

    constructor(private _formBuilder: FormBuilder,
                private _translationService: TranslationService,
                private _notificationService: NotificationService,
                private _workspaceService: WorkspaceService) {
        this.dataSource = new TranslationsDataSource(_translationService, _notificationService, _formBuilder);
    }

    @Input()
    public get searchRequest(): TranslationsSearchRequest {
        return this._searchRequest;
    }

    public set searchRequest(request: TranslationsSearchRequest) {
        this._searchRequest = request;

        this.dataSource.setRequest(request);
    }

    public ngOnInit(): void {
        this.dataSource.form
            .valueChanges
            .pipe(
                takeUntil(this._destroyed$),
                auditTime(5000)
            )
            .subscribe(() => {
                for (const rowForm of this.dataSource.form.controls) {
                    if (rowForm.dirty) {
                        // TODO save
                    }
                }
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public trackByFn(indexInSource, index, item) {
        return indexInSource;
    }

    public get actionInProgress(): boolean {
        return this.dataSource.loading;
    }

    public get unsavedChanges(): boolean {
        return this.dataSource.form.dirty;
    }

    public get spreadRowClass(): string {
        return `app-scroller-spread-row-${this.searchRequest.locales.length}`;
    }

    public getWorkspace(rowForm: FormGroup): Observable<EnrichedWorkspace> {
        return this._workspaceService.getEnrichedWorkspace(this.dataSource.getWorkspace(rowForm));
    }

    public getBundleFile(rowForm: FormGroup): Observable<BundleFile> {
        const workspace = this.dataSource.getWorkspace(rowForm);
        const bundleFile = this.dataSource.getBundleFile(rowForm);

        return this._workspaceService.getWorkspaceBundleFile(workspace, bundleFile);
    }
}
