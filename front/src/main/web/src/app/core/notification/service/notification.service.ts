import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material';
import {ErrorMessagesNotificationComponent} from "../component/validation-result-notification/error-messages-notification.component";
import {ErrorMessagesDto} from "../../../api";
import {TranslateService} from "@ngx-translate/core";
import {instanceOfErrorMessages, instanceOfHttpError} from "../../shared/utils/error-utils";
import {
    NotificationSnackbarComponent,
    NotificationType
} from '../component/notification-snackbar/notification-snackbar.component';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private _snackBar: MatSnackBar,
                private translateService: TranslateService) {
    }

    public displayErrorMessage(message?: string, cause?: any): void {
        if (instanceOfHttpError(cause)) {
            return this.displayErrorMessage(message, cause.error);
        } else if (instanceOfErrorMessages(cause)) {
            this.displayErrorMessages(this.translateService.instant(message), <ErrorMessagesDto>cause);
        } else {
            this.displayTextErrorMessage(this.translateService.instant(message), cause);
        }
    }

    public displayInfoMessage(message: string) {
        this._snackBar.openFromComponent(NotificationSnackbarComponent, {
            duration: 10000,
            data: {message: this.translateService.instant(message), type: NotificationType.INFO}
        });
    }

    private displayTextErrorMessage(message: string, cause: any) {
        console.error(message, cause);

        let text = "";

        if (message && message.trim().length > 0) {
            text += message;
        }

        if (cause && (typeof message === 'string') && cause.trim().length > 0) {
            if (text.length > 0) {
                text += " ";
            }

            text += cause;
        }

        this._snackBar.openFromComponent(NotificationSnackbarComponent, {
            duration: 10000,
            data: {message: text, type: NotificationType.WARNING}
        });
    }

    private displayErrorMessages(message: string, errorMessages: ErrorMessagesDto): void {
        console.error(message, errorMessages);

        this._snackBar.openFromComponent(ErrorMessagesNotificationComponent,
            {
                data: {errorMessages: errorMessages, message: message},
                panelClass: ['snack-bar', 'mat-toolbar', 'mat-theme']
            });
    }
}
