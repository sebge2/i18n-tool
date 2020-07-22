import {Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material';
import {ErrorNotificationComponent} from "../component/error-notification/error-notification.component";
import {ErrorMessagesNotificationComponent} from "../component/validation-result-notification/error-messages-notification.component";
import {ErrorMessagesDto} from "../../../api";
import {TranslateService} from "@ngx-translate/core";
import {HttpErrorResponse} from "@angular/common/http";
import * as _ from "lodash";

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    constructor(private _snackBar: MatSnackBar,
                private translateService: TranslateService) {
    }

    public displayErrorMessage(message?: string, cause?: any): void {
        if (NotificationService.instanceOfHttpError(cause)) {
            return this.displayErrorMessage(message, cause.error);
        } else if (NotificationService.instanceOfErrorMessages(cause)) {
            this.displayErrorMessages(this.translateService.instant(message), <ErrorMessagesDto>cause);
        } else if (typeof "cause" === "string") {
            this.displayTextErrorMessage(this.translateService.instant(message), cause);
        } else {
            this.displayTextErrorMessage(this.translateService.instant(message), cause);
        }
    }

    private displayTextErrorMessage(message: string, cause: any) {
        console.error(message, cause);

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

        this._snackBar.openFromComponent(ErrorNotificationComponent, {
            duration: 10000,
            data: {message: text}
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

    private static instanceOfErrorMessages(object: any): object is ErrorMessagesDto {
        return object && 'messages' in object && 'id' in object && 'time' in object;
    }

    private static instanceOfHttpError(error: any): error is HttpErrorResponse {
        return _.get(error, 'name') === 'HttpErrorResponse';
    }
}
