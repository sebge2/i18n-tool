import { ChangeDetectorRef, Component } from '@angular/core';
import { createImportedFile, FileExtension } from '@i18n-core-shared';
import { SnapshotService } from '../../../../service/snapshot.service';
import { NotificationService } from '@i18n-core-notification';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as _ from 'lodash';

@Component({
  selector: 'app-snapshot-import-form',
  templateUrl: './snapshot-import-form.component.html',
  styleUrls: ['./snapshot-import-form.component.css'],
})
export class SnapshotImportFormComponent {
  readonly form: FormGroup;
  readonly allowedFileExtensions: FileExtension[] = [FileExtension.ZIP];
  importInProgress = false;

  constructor(
    private _formBuilder: FormBuilder,
    private cd: ChangeDetectorRef,
    private _snapshotService: SnapshotService,
    private _notificationService: NotificationService
  ) {
    this.form = _formBuilder.group({
      file: [null, Validators.required],
      encryptionPassword: [null],
    });
  }

  get fileSelected(): boolean {
    return !!this.form.controls['file'].value;
  }

  get fileName(): string {
    return _.get(this.form.controls['file'].value, ['file', 'name']);
  }

  onFileChange(files: FileList) {
    const file = files.item(0);
    const importedFile = createImportedFile(file, this.allowedFileExtensions);

    if (!importedFile) {
      console.error('Cannot import file.', file);
    } else {
      this.form.controls['file'].setValue(importedFile);
    }
  }

  onImport() {
    this.importInProgress = true;

    this._snapshotService
      .importSnapshot(this.form.controls['file'].value.file, this.form.controls['encryptionPassword'].value)
      .toPromise()
      .then(() => {
        this.form.controls['file'].setValue(null);
        this.form.controls['encryptionPassword'].setValue(null);
      })
      .catch((error) => {
        console.error('Error while importing a snapshot.', error);
        this._notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.IMPORT', error);
      })
      .finally(() => (this.importInProgress = false));
  }
}
