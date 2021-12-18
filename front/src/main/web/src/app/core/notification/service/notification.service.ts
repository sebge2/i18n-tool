import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material';
import { ErrorMessagesNotificationComponent } from '../component/validation-result-notification/error-messages-notification.component';
import { ErrorMessagesDto } from '../../../api';
import { TranslateService } from '@ngx-translate/core';
import {instanceOfErrorMessages, instanceOfHttpError, TranslationKey} from '@i18n-core-shared';
import {
  NotificationSnackbarComponent,
  NotificationType,
} from '../component/notification-snackbar/notification-snackbar.component';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  constructor(private _snackBar: MatSnackBar, private _translateService: TranslateService) {}

  displayErrorMessage(message?: string, cause?: any): void {
    if (instanceOfHttpError(cause)) {
      return this.displayErrorMessage(message, cause.error);
    } else if (instanceOfErrorMessages(cause)) {
      this._displayErrorMessages(this._translateService.instant(message), <ErrorMessagesDto>cause);
    } else {
      this._displayTextErrorMessage(this._translateService.instant(message), cause);
    }
  }

  displayInfoMessage(message: string | TranslationKey) {
    const messageText = message instanceof TranslationKey
        ? this._translateService.instant(message.key)
        : message;

    this._snackBar.openFromComponent(NotificationSnackbarComponent, {
      duration: 10000,
      data: { message: this._translateService.instant(messageText), type: NotificationType.INFO },
    });
  }

  private _displayTextErrorMessage(message: string, cause: any) {
    console.error(message, cause);

    let text = '';

    if (message && message.trim().length > 0) {
      text += message;
    }

    if (cause && typeof cause === 'string' && cause.trim().length > 0) {
      if (text.length > 0) {
        text += ' ';
      }

      text += cause;
    }

    this._snackBar.openFromComponent(NotificationSnackbarComponent, {
      duration: 10000,
      data: { message: text, type: NotificationType.WARNING },
    });
  }

  private _displayErrorMessages(message: string, errorMessages: ErrorMessagesDto): void {
    console.error(message, errorMessages);

    this._snackBar.openFromComponent(ErrorMessagesNotificationComponent, {
      data: { errorMessages: errorMessages, message: message },
      panelClass: ['snack-bar', 'mat-toolbar', 'mat-theme'],
    });
  }
}
