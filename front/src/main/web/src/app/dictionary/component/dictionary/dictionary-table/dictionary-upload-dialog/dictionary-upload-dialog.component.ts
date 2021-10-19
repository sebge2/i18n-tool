import {Component, OnDestroy} from '@angular/core';
import {MatDialogRef} from "@angular/material/dialog";
import {FileExtension} from "../../../../../core/shared/model/file-extension.model";
import {ImportedFile} from "../../../../../core/shared/model/imported-file.model";
import {DictionaryService} from "../../../../service/dictionary.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {NotificationService} from "../../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-dictionary-upload-dialog',
    templateUrl: './dictionary-upload-dialog.component.html',
    styleUrls: ['./dictionary-upload-dialog.component.css']
})
export class DictionaryUploadDialogComponent implements OnDestroy {

    public readonly allowedFileExtensions: FileExtension[] = [FileExtension.CSV];
    public uploading: boolean = false;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private _dialogRef: MatDialogRef<DictionaryUploadDialogComponent>,
                private _dictionaryService: DictionaryService,
                private _notificationService: NotificationService) {
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public onCancel(): void {
        this._dialogRef.close();
    }

    public onFileDropped(file: ImportedFile): void {
        this.onFile(file);
    }

    public onFileImported(file: ImportedFile): void {
        this.onFile(file);
    }

    public onFile(file: ImportedFile): void {
        this.uploading = true;

        this._dictionaryService.upload(file)
            .pipe(takeUntil(this._destroyed$))
            .toPromise()
            .then(() => {
                this._dialogRef.close();
            })
            .catch((error) => {
                this._notificationService.displayErrorMessage('DICTIONARY.ERROR.UPLOAD', error);
            })
            .finally(() => {
                this.uploading = false;
            });
    }
}
