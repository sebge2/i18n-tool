import {Component, Inject} from '@angular/core';
import {MAT_SNACK_BAR_DATA} from "@angular/material/snack-bar";

export enum NotificationType {
    WARNING,

    INFO
}

export interface NotificationData {
    message: string;
    type: NotificationType;
}

@Component({
    selector: 'app-notification-snackbar',
    templateUrl: './notification-snackbar.component.html',
    styleUrls: ['./notification-snackbar.component.css']
})
export class NotificationSnackbarComponent {

    constructor(@Inject(MAT_SNACK_BAR_DATA) public data: NotificationData) {
    }

    public get iconType(): string{
        switch (this.data.type) {
            case NotificationType.INFO:
                return 'info';
            case NotificationType.WARNING:
                return 'warning';
            default:
                return '';
        }
    }

    public get iconClass(): string{
        switch (this.data.type) {
            case NotificationType.INFO:
                return 'info';
            case NotificationType.WARNING:
                return 'warning';
            default:
                return '';
        }
    }

}
