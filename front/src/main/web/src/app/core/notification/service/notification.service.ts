import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material';
import {ErrorNotificationComponent} from "../component/error-notification/error-notification.component";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private snackBar: MatSnackBar) {
    }

    displayErrorMessage(message?: string, cause?: string): void {
        let text = "";

        if (message && message.trim().length > 0) {
            text += message;
        }

        if (cause && cause.trim().length > 0) {
            if (text.length > 0) {
                text += " ";
            }

            text += cause;
        }

        console.log(text);

        this.snackBar.openFromComponent(ErrorNotificationComponent, {
            duration: 10000,
            data: {message: text}
        });
    }
}
