import {ChangeDetectorRef, Component} from '@angular/core';
import {createImportedFile, FileExtension} from "../../../../../core/shared/model/file-extension.model";
import {SnapshotService} from "../../../../service/snapshot.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import * as _ from "lodash";
import {MouseEventUtils} from "../../../../../core/shared/utils/mouse-event-utils";

@Component({
    selector: 'app-snapshot-import-form',
    templateUrl: './snapshot-import-form.component.html',
    styleUrls: ['./snapshot-import-form.component.css']
})
export class SnapshotImportFormComponent {

    public readonly form: FormGroup;
    public readonly allowedFileExtensions: FileExtension[] = [FileExtension.ZIP];
    public importInProgress = false;

    constructor(private _formBuilder: FormBuilder,
                private cd: ChangeDetectorRef,
                private _snapshotService: SnapshotService,
                private _notificationService: NotificationService) {
        this.form = _formBuilder.group({
            file: [null, Validators.required],
            encryptionPassword: [null],
        });
    }

    public get fileSelected(): boolean {
        return !!this.form.controls['file'].value;
    }

    public get fileName(): string {
        return _.get(this.form.controls['file'].value, ['file', 'name']);
    }

    public onFileChange(files: FileList) {
        const file = files.item(0);
        const importedFile = createImportedFile(file, this.allowedFileExtensions);

        if (!importedFile) {
            console.error("Cannot import file.", file);
        } else {
            this.form.controls['file'].setValue(importedFile);
        }
    }

    public onImport() {
        this.importInProgress = true;

        this._snapshotService
            .importSnapshot(this.form.controls['file'].value.file, this.form.controls['encryptionPassword'].value)
            .toPromise()
            .then(() => {
                this.form.controls['file'].setValue(null);
                this.form.controls['encryptionPassword'].setValue(null);
            })
            .catch(error => {
                console.error('Error while importing a snapshot.', error);
                this._notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.IMPORT', error);
            })
            .finally(() => this.importInProgress = false);
    }
}
