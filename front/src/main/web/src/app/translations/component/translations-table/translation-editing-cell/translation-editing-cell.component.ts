import {Component, Input, OnInit} from '@angular/core';
import {BundleKeyTranslation} from "../../../model/workspace/bundle-key-translation.model";
import {FormBuilder, FormGroup} from "@angular/forms";
import {TranslationService} from "../../../../api";

@Component({
    selector: 'app-translation-editing-cell',
    templateUrl: './translation-editing-cell.component.html',
    styleUrls: ['./translation-editing-cell.component.css']
})
export class TranslationEditingCellComponent implements OnInit {

    public form: FormGroup;
    private _translation: BundleKeyTranslation;

    constructor(private _formBuilder: FormBuilder,
                private _translationService: TranslationService) {
        this.form = _formBuilder.group({
            value: _formBuilder.control(null)
        });
    }

    public ngOnInit() {
    }

    @Input()
    public get translation(): BundleKeyTranslation {
        return this._translation;
    }

    public set translation(translation: BundleKeyTranslation) {
        this._translation = translation;

        this.onReset();
    }

    public onReset() {
        this.form.controls['value'].setValue(this.translation.currentValue);
        this.form.markAsPristine();
    }
}
