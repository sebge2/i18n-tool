import {Component, Input} from '@angular/core';
import {instanceOfErrorMessages, instanceOfHttpError} from "@i18n-core-shared";
import {ErrorMessagesDto} from "../../../../../api";

@Component({
    selector: 'app-error-display',
    templateUrl: './error-display.component.html',
})
export class ErrorDisplayComponent {

    @Input() unidentifiedErrorLabel: string;
    @Input() identifiedErrorLabel: string;

    unidentifiedError: any;
    identifiedError: ErrorMessagesDto;

    private _error: any;

    constructor() {
    }

    @Input()
    public get error(): any {
        return this._error;
    }

    set error(value: any) {
        this.handleError(value);
    }

    private handleError(value: any) {
        if (instanceOfHttpError(value)) {
            return this.handleError(value.error);
        } else if (instanceOfErrorMessages(value)) {
            this.identifiedError = <ErrorMessagesDto>value;
        } else {
            this.unidentifiedError = value;
        }
    }
}
