import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material';
import {ErrorNotificationComponent} from "../component/error-notification/error-notification.component";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private snackBar: MatSnackBar) {
    }

    displayErrorMessage(message: string): void {
        console.log(message);

        this.snackBar.openFromComponent(ErrorNotificationComponent, {
            duration: 10000,
            data: {message: message}
        });
    }
}
