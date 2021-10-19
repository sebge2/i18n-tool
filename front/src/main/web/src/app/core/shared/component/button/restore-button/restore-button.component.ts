import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import * as _ from "lodash";
import {RestoreButtonConfirmationComponent} from "./restore-button-confirmation/restore-button-confirmation.component";

@Component({
    selector: 'app-restore-button',
    templateUrl: './restore-button.component.html',
})
export class RestoreButtonComponent {

    @Input() public restoreInProgress: boolean;
    @Input() public disabled: boolean;
    @Input() public confirmationMessage: string;
    @Output() public restore = new EventEmitter<void>();

    constructor(private _dialog: MatDialog) {
    }

    public onRestore() {
        if (this.confirmationMessage) {
            const dialogRef = this._dialog.open(
                RestoreButtonConfirmationComponent,
                {
                    width: '600px',
                    data: {confirmationMessage: this.confirmationMessage}
                }
            );

            dialogRef
                .afterClosed()
                .subscribe((deleteConfirmed: string) => {
                    if (_.isEqual(deleteConfirmed, 'CONFIRMED')) {
                        this.restore.emit();
                    }
                });
        } else {
            this.restore.emit();
        }
    }
}
