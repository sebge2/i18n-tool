import {Component, Input} from '@angular/core';
import {MatDialog} from "@angular/material/dialog";
import {WorkspacesStartReviewDialogComponent} from "../workspaces-start-review-dialog/workspaces-start-review-dialog.component";

@Component({
    selector: 'app-form-workspaces-start-review-button',
    templateUrl: './form-workspaces-start-review-button.component.html'
})
export class FormWorkspacesStartReviewButtonComponent {

    @Input() public disabled: boolean = false;

    constructor(private _dialog: MatDialog) {
    }

    public onClick() {
        this._dialog.open(WorkspacesStartReviewDialogComponent, {
            width: '400px',
            data: {}
        });

    }
}
