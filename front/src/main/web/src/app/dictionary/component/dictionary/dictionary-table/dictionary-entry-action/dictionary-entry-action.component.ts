import {Component, Input} from '@angular/core';
import * as _ from "lodash";
import {DictionaryService} from "../../../../service/dictionary.service";
import {NotificationService} from "../../../../../core/notification/service/notification.service";
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-dictionary-entry-action',
    templateUrl: './dictionary-entry-action.component.html',
})
export class DictionaryEntryActionComponent {

    private _deleteInProgress: boolean = false;
    private _translationAsString: string = "";
    private _form: FormGroup;

    constructor(private _dictionaryService: DictionaryService,
                private _notificationService: NotificationService) {
    }

    @Input()
    get form(): FormGroup{
        return this._form;
    }

    set form(value: FormGroup) {
        this._form = value;
        this._translationAsString = _.chain(_.keys(value.controls))
            .filter(controlName => controlName !== 'id')
            .map(controlName => value.get(controlName).value)
            .join(', ')
            .value();
    }

    get translationAsString(): string {
        return this._translationAsString;
    }

    get deleteInProgress(): boolean {
        return this._deleteInProgress;
    }

    public onDelete(): void {
        this._deleteInProgress = true;
        this._dictionaryService
            .delete(this.form.get('id').value)
            .toPromise()
            .catch(error => this._notificationService.displayErrorMessage('DICTIONARY.ERROR.DELETE', error))
            .finally(() => this._deleteInProgress = false);
    }

}
