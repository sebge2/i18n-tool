import {Component, OnDestroy, OnInit} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {moveItemInArray, transferArrayItem} from "@angular/cdk/drag-drop";
import {TranslationLocaleService} from "../../../../../translations/service/translation-locale.service";
import {DictionaryService} from "../../../../service/dictionary.service";
import {TranslationLocale} from "../../../../../translations/model/translation-locale.model";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {combineLatest, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import * as _ from "lodash";

@Component({
    selector: 'app-dictionary-settings-dialog',
    templateUrl: './dictionary-settings-dialog.component.html',
    styleUrls: ['./dictionary-settings-dialog.component.scss'],
})
export class DictionarySettingsDialogComponent implements OnInit, OnDestroy {

    public availableLocales: TranslationLocale [] = [];
    public selectedLocales: TranslationLocale [] = [];
    public applying: boolean = false;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private _dialogRef: MatDialogRef<DictionarySettingsDialogComponent>,
                private _localeService: TranslationLocaleService,
                private _dictionaryService: DictionaryService,
                private _notificationService: NotificationService) {
    }

    public ngOnInit(): void {
        combineLatest([this._localeService.getAvailableLocales(), this._dictionaryService.getLocales$()])
            .pipe(takeUntil(this._destroyed$))
            .subscribe(([allLocales, selectedLocales]) => {
                this.availableLocales = _.chain(allLocales)
                    .filter(locale => !_.includes(selectedLocales, locale))
                    .value();
            });

        this._dictionaryService.getLocales$()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((selectedLocales: TranslationLocale[]) => this.selectedLocales = selectedLocales);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onSelect(event: { fromIndex: number; toIndex: number }): void {
        transferArrayItem(
            this.availableLocales,
            this.selectedLocales,
            event.fromIndex,
            event.toIndex,
        );
    }

    public onUnselect(event: { fromIndex: number; toIndex: number }) {
        transferArrayItem(
            this.selectedLocales,
            this.availableLocales,
            event.fromIndex,
            event.toIndex,
        );
    }

    public onSelectAll() {
        this.selectedLocales = _.concat(this.selectedLocales, this.availableLocales);
        this.availableLocales = [];
    }

    public onUnselectAll() {
        this.availableLocales = _.concat(this.availableLocales, this.selectedLocales);
        this.selectedLocales = [];
    }

    public onMoveSelected(event: { fromIndex: number; toIndex: number }): void {
        moveItemInArray(this.selectedLocales, event.fromIndex, event.toIndex);
    }

    public onMoveAvailable(event: { fromIndex: number; toIndex: number }): void {
        moveItemInArray(this.availableLocales, event.fromIndex, event.toIndex);
    }

    public onApply() {
        this.applying = true;

        this._dictionaryService
            .setPreferredLocales(this.selectedLocales)
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .then(_ => this._dialogRef.close({}))
            .catch(error => {
                console.error('Error while saving dictionary preferences.', error);
                this._notificationService.displayErrorMessage('DICTIONARY.ERROR.SAVING_PREFERENCES', error);
            })
            .finally(() => this.applying = false);
    }
}
