import {Component} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {SnapshotService} from "../../../../service/snapshot.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";

@Component({
    selector: 'app-snapshot-creation-form',
    templateUrl: './snapshot-creation-form.component.html',
    styleUrls: ['./snapshot-creation-form.component.css']
})
export class SnapshotCreationFormComponent {

    public creationInProgress = false;
    public readonly form: FormGroup;

    constructor(private _formBuilder: FormBuilder,
                private _snapshotService: SnapshotService,
                private _notificationService: NotificationService) {
        this.form = _formBuilder.group({
            comment: [null, Validators.required],
            encryptionPassword: [null],
        });
    }

    public onCreate() {
        this.creationInProgress = true;

        this._snapshotService
            .createSnapshot(this.form.controls['comment'].value, this.form.controls['encryptionPassword'].value)
            .toPromise()
            .catch(error => {
                console.error('Error while creating a snapshot.', error);
                this._notificationService.displayErrorMessage('ADMIN.SNAPSHOTS.ERROR.CREATE', error);
            })
            .finally(() => this.creationInProgress = false);
    }
}
