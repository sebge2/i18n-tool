import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {BundleKeyTranslation} from "../../../model/edition/bundle-key-translation.model";

@Component({
    selector: 'app-translation-editing-cell',
    templateUrl: './translation-editing-cell.component.html',
    styleUrls: ['./translation-editing-cell.component.css']
})
export class TranslationEditingCellComponent implements OnInit {

    @Output()
    valueChange : EventEmitter<BundleKeyTranslation> = new EventEmitter<BundleKeyTranslation>();

    @Input()
    value: BundleKeyTranslation;

    private _textValue : String;

    constructor() {
    }

    ngOnInit() {
        this._textValue = this.value.currentValue();
    }

    get textValue(): String {
        return this._textValue;
    }

    set textValue(value: String) {
        this._textValue = value;

        this.value.updatedValue = value;
        this.valueChange.emit(this.value);
    }
}
