import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-restore-button-confirmation',
  templateUrl: './restore-button-confirmation.component.html',
  styleUrls: ['./restore-button-confirmation.component.css'],
})
export class RestoreButtonConfirmationComponent {
  public disabled: boolean = true;

  constructor(
    private _dialogRef: MatDialogRef<RestoreButtonConfirmationComponent>,
    @Inject(MAT_DIALOG_DATA) public config: { confirmationMessage: string }
  ) {}

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
