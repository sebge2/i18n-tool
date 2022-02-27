import {Component, OnDestroy, OnInit} from '@angular/core';
import {ExternalTranslatorConfig, ExternalTranslatorService} from "@i18n-dictionary";
import {MatTableDataSource} from "@angular/material/table";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {NotificationService} from "@i18n-core-notification";
import {MatDialog} from "@angular/material/dialog";
import {RepositoryAddWizardComponent} from "../repositories/repository-list/repository-add-wizard/repository-add-wizard.component";
import {ExternalTranslatorAddPopupComponent} from "./external-translator-add-popup/external-translator-add-popup.component";

@Component({
    selector: 'app-external-translators',
    templateUrl: './external-translators.component.html',
})
export class ExternalTranslatorsComponent implements OnInit, OnDestroy {

    readonly dataSource = new MatTableDataSource<ExternalTranslatorConfig>();
    deleteInProgress: boolean = false;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private _translatorService: ExternalTranslatorService,
                private _notificationService: NotificationService,
                private _dialog: MatDialog) {
    }

    ngOnInit(): void {
        this._translatorService
            .getConfigs$()
            .pipe(takeUntil(this._destroyed$))
            .subscribe((configs) => (this.dataSource.data = configs));
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    onOpenLink(linkUrl: string): void {
        window.open(linkUrl, '_blank');
    }

    onDelete(config: ExternalTranslatorConfig): void {
        this.deleteInProgress = true;

        this._translatorService
            .delete$(config.id)
            .toPromise()
            .catch(error => {
                console.error('Error while deleting the external translator configuration.', error);
                this._notificationService.displayErrorMessage('ADMIN.EXTERNAL_TRANSLATORS.ERROR.DELETE', error);
            })
            .finally(() => this.deleteInProgress = false);
    }

    onAdd() {
        this._dialog.open(ExternalTranslatorAddPopupComponent);
    }
}
