import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { FormDeleteButtonConfirmationComponent } from './form-delete-button-confirmation/form-delete-button-confirmation.component';
import * as _ from 'lodash';
import { TranslationKey } from '../../../model/translation-key.model';

@Component({
  selector: 'app-form-delete-button',
  templateUrl: './form-delete-button.component.html',
})
export class FormDeleteButtonComponent {
  @Input() public deleteInProgress: boolean;
  @Input() public disabled: boolean;
  @Input() public confirmationMessage: string | TranslationKey;
  @Output() public delete = new EventEmitter<void>();

  constructor(private _dialog: MatDialog) {}

  public onDelete() {
    if (this.confirmationMessage) {
      const dialogRef = this._dialog.open(FormDeleteButtonConfirmationComponent, {
        width: '600px',
        data: { confirmationMessage: this.confirmationMessage },
      });

      dialogRef.afterClosed().subscribe((deleteConfirmed: string) => {
        if (_.isEqual(deleteConfirmed, 'CONFIRMED')) {
          this.delete.emit();
        }
      });
    } else {
      this.delete.emit();
    }
  }
}
