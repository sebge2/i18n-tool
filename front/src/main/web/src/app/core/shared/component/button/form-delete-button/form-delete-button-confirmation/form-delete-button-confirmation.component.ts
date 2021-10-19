import {Component, Inject} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {TranslationKey} from "../../../../model/translation-key.model";

@Component({
    selector: 'app-form-delete-button-confirmation',
    templateUrl: './form-delete-button-confirmation.component.html',
    styleUrls: ['./form-delete-button-confirmation.component.css']
})
export class FormDeleteButtonConfirmationComponent {

    public disabled: boolean = true;
    public confirmationMessage: string;
    public confirmationMessageKey: string;

    constructor(private _dialogRef: MatDialogRef<FormDeleteButtonConfirmationComponent>,
                @Inject(MAT_DIALOG_DATA) public config: { confirmationMessage: string | TranslationKey }) {
        if(config.confirmationMessage instanceof TranslationKey){
            this.confirmationMessageKey = config.confirmationMessage.key;
        } else {
            this.confirmationMessage = config.confirmationMessage;
        }
    }

    public onElapsed() {
        this.disabled = false;
    }

    public onConfirm() {
        this._dialogRef.close('CONFIRMED');
    }

    public onCancel() {
        this._dialogRef.close(null);
    }
}
