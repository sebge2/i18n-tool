import { Component, Inject, ViewEncapsulation } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { ErrorMessagesDto } from '../../../../api';

@Component({
  selector: 'app-error-messages-notification',
  templateUrl: './error-messages-notification.component.html',
  styleUrls: ['./error-messages-notification.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class ErrorMessagesNotificationComponent {
  errorMessages: ErrorMessagesDto;
  message: string;

  constructor(
    @Inject(MAT_SNACK_BAR_DATA) private _data: { errorMessages: ErrorMessagesDto; message: string },
    public snackBarRef: MatSnackBarRef<ErrorMessagesNotificationComponent>
  ) {
    this.errorMessages = _data.errorMessages;
    this.message = _data.message;
  }
}
