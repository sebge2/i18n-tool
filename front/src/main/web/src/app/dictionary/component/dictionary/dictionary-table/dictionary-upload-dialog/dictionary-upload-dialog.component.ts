import { Component, OnDestroy } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FileExtension } from '@i18n-core-shared';
import { ImportedFile } from '@i18n-core-shared';
import { DictionaryService } from '../../../../service/dictionary.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-dictionary-upload-dialog',
  templateUrl: './dictionary-upload-dialog.component.html',
  styleUrls: ['./dictionary-upload-dialog.component.css'],
})
export class DictionaryUploadDialogComponent implements OnDestroy {
  public readonly allowedFileExtensions: FileExtension[] = [FileExtension.CSV];
  public uploading: boolean = false;

  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private _dialogRef: MatDialogRef<DictionaryUploadDialogComponent>,
    private _dictionaryService: DictionaryService,
    private _notificationService: NotificationService
  ) {}

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
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

    this._dictionaryService
      .upload(file)
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
