import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {DictionaryTableState} from "../../../model/dictionary-table-state.model";
import {combineLatest, Observable, Subject} from "rxjs";
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {FormTableDataSource} from "../../../../core/shared/component/table/table.component";
import {DictionaryService} from "../../../service/dictionary.service";
import {FormBuilder, FormControl, FormGroup} from "@angular/forms";
import {DictionaryEntry} from "../../../model/dictionary-entry.model";
import {auditTime, map, takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {DictionaryEntryPatchDto} from "../../../../api";
import {NotificationService} from "../../../../core/notification/service/notification.service";
import {HttpUtils} from "../../../../core/shared/utils/http-utils";
import {HttpResponse} from "@angular/common/http";
import {BlobUtils} from "../../../../core/shared/utils/blob-utils";
import {MatDialog} from "@angular/material/dialog";
import {DictionaryUploadDialogComponent} from "./dictionary-upload-dialog/dictionary-upload-dialog.component";
import {DictionarySettingsDialogComponent} from "./dictionary-settings-dialog/dictionary-settings-dialog.component";
import {FormDeleteButtonConfirmationComponent} from "../../../../core/shared/component/button/form-delete-button/form-delete-button-confirmation/form-delete-button-confirmation.component";
import {TranslationKey} from "../../../../core/shared/model/translation-key.model";

class DirtyTranslationForm {

    constructor(public entryId: string,
                public localeId: string,
                public translation: string,
                public translationForm: FormControl) {
    }
}

export const atLeastOneTranslationValidator = () => {
    return (group: FormGroup) => {
        let nonNullTranslation = _.chain(_.keys(group.controls))
            .filter(controlName => controlName !== 'id')
            .find(controlName => group.get(controlName).value)
            .value();

        return nonNullTranslation
            ? null
            : {atLeastOneTranslation: true};
    };
};

@Component({
    selector: 'app-dictionary-table',
    templateUrl: './dictionary-table.component.html',
    styleUrls: ['./dictionary-table-component.scss']
})
export class DictionaryTableComponent implements OnInit, OnDestroy {

    @Input() public state: DictionaryTableState = new DictionaryTableState();

    public readonly locales: Observable<TranslationLocale[]>;
    public readonly dataSource: FormTableDataSource;
    public moreActionInProgress: boolean = false;

    private _destroyed$ = new Subject<void>();

    constructor(private _dictionaryService: DictionaryService,
                private _notificationService: NotificationService,
                private _formBuilder: FormBuilder,
                private _dialog: MatDialog) {
        this.locales = this._dictionaryService.getLocales$();
        this.dataSource = new FormTableDataSource(_formBuilder.array([]));
    }

    public ngOnInit(): void {
        this.state.notifyLoading(true);

        combineLatest([this._dictionaryService.getDictionary$(), this.locales])
            .pipe(takeUntil(this._destroyed$))
            .subscribe(([entries, locales]) => {
                this.state.notifyLoading(true);

                this._fillForm(locales, entries);

                // TODO sorting
                this.state.notifyLoading(false);
            });

        this.dataSource.form
            .valueChanges
            .pipe(
                takeUntil(this._destroyed$),
                auditTime(5000)
            )
            .subscribe(() => {
                this.state.notifySaving(true);

                this._saveTranslations(this.findDirtyTranslations());
            });

        this.dataSource.form.valueChanges
            .pipe(
                takeUntil(this._destroyed$),
                map(() => this.dataSource.form.dirty),
            )
            .subscribe(dirty => this.state.notifyUnsavedChanges(dirty));
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public localeId(index: number, entry: DictionaryEntry) {
        return entry.id;
    }

    public onSettingsOpen(): void {
       this._dialog.open(DictionarySettingsDialogComponent, {data: {}});
    }

    public onDownload(): void {
        this.moreActionInProgress = true;

        this._dictionaryService.download()
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .then((response: HttpResponse<Blob>) => {
                const fileName = HttpUtils.getContentDispositionFileName(response);

                BlobUtils.downloadBlob(response.body, fileName);
            })
            .catch(error => {
                console.error('Error while downloading translations.', error);
                this._notificationService.displayErrorMessage('DICTIONARY.ERROR.DOWNLOAD', error);
            })
            .finally(() => {
                this.moreActionInProgress = false;
            });
    }

    public onUpload(): void {
        this._dialog.open(DictionaryUploadDialogComponent, {data: {}});
    }

    public onDeleteAll(): void {
        const dialogRef = this._dialog.open(
            FormDeleteButtonConfirmationComponent,
            {
                width: '600px',
                data: {confirmationMessage: TranslationKey.forKey('DICTIONARY.TABLE.DELETE_ALL_CONFIRMATION')}
            }
        );

        dialogRef
            .afterClosed()
            .subscribe((deleteConfirmed: string) => {
                if (_.isEqual(deleteConfirmed, 'CONFIRMED')) {
                    this._onDeleteAllConfirmed();
                }
            });
    }

    private _fillForm(locales: TranslationLocale[], entries: DictionaryEntry[]) {
        this.dataSource.form.clear();

        this.dataSource.form.push(this._createAddTranslationForm(locales));

        _.forEach(entries, entry => this.dataSource.form.push(this._createEntryForm(entry, locales)));
    }

    private _createAddTranslationForm(locales: TranslationLocale[]): FormGroup {
        const addRowGroup = this._formBuilder.group({}, {validators: atLeastOneTranslationValidator()});

        _.forEach(locales, locale => addRowGroup.addControl(locale.id, this._formBuilder.control(null)));

        return addRowGroup;
    }

    private _createEntryForm(entry, locales: TranslationLocale[]): FormGroup {
        const rowGroup = this._formBuilder.group({id: entry.id});

        _.forEach(locales, locale => rowGroup.addControl(locale.id, this._formBuilder.control(entry.getTranslationForLocaleEntity(locale))));

        return rowGroup;
    }

    private findDirtyTranslations(): DirtyTranslationForm[] {
        return <DirtyTranslationForm[]>_.flatten(
            this.dataSource.form.controls
                .filter(rowForm => rowForm.dirty)
                .filter((rowForm: FormGroup) => !_.isNil(rowForm.get('id')))
                .map((rowForm: FormGroup) => {
                    return _.forEach(_.keys(rowForm.controls))
                        .filter((controlName: string) => rowForm.controls[controlName].dirty)
                        .map((controlName: string) =>
                            new DirtyTranslationForm(
                                rowForm.get('id').value,
                                controlName,
                                rowForm.controls[controlName].value,
                                <FormControl>rowForm.controls[controlName]
                            )
                        )
                })
        );
    }

    private _saveTranslations(dirtyTranslations: DirtyTranslationForm[]): void {
        this._dictionaryService
            .updateTranslations(
                dirtyTranslations.map(dirtyTranslation => <DictionaryEntryPatchDto>{
                    id: dirtyTranslation.entryId,
                    translations: {
                        [dirtyTranslation.localeId]: dirtyTranslation.translation
                    }
                })
            )
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .then(() => this.updateDirtyFlags(dirtyTranslations))
            .catch(error => {
                console.error('Error while updating translations.', error);
                this._notificationService.displayErrorMessage('DICTIONARY.ERROR.UPDATE', error);
            })
            .finally(() => this.state.notifySaving(false));
    }

    private updateDirtyFlags(dirtyTranslations: DirtyTranslationForm[]) {
        dirtyTranslations.forEach(dirtyTranslation => {
            if (_.eq(dirtyTranslation.translation, dirtyTranslation.translationForm.value)) {
                dirtyTranslation.translationForm.markAsPristine();
            }
        });

        this.state.notifyUnsavedChanges(this.dataSource.form.dirty);
    }

    private _onDeleteAllConfirmed() {
        this.moreActionInProgress = true;

        this._dictionaryService
            .deleteAll()
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .catch(error => {
                console.error('Error while deleting translations.', error);
                this._notificationService.displayErrorMessage('DICTIONARY.ERROR.DELETE_ALL', error);
            })
            .finally(() => {
                this.moreActionInProgress = false;
            })
    }
}
