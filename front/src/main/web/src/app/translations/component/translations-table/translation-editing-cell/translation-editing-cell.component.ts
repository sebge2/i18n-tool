import {Component, Input, OnInit} from '@angular/core';
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-translation-editing-cell',
    templateUrl: './translation-editing-cell.component.html',
    styleUrls: ['./translation-editing-cell.component.css']
})
export class TranslationEditingCellComponent implements OnInit {

    private _form: FormGroup;

    constructor() {
    }

    public ngOnInit() {
    }

    @Input()
    public get form(): FormGroup {
        return this._form;
    }

    public set form(form: FormGroup) {
        this._form = form;
    }

    public onReset() {
        this.form.controls['value'].setValue(this.originalValue);
        this.form.markAsPristine();
    }

    public get originalValue(): string {
        return this.form.controls['originalValue'].value;
    }
}
