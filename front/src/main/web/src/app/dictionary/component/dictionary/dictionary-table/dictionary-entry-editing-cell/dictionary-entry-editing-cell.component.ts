import {Component, Input} from '@angular/core';
import {TranslationLocale} from "../../../../../translations/model/translation-locale.model";
import {FormGroup} from "@angular/forms";

export interface DictionaryEntryEditorCoordinate {
    entryForm: FormGroup;
    locale: TranslationLocale;
}

@Component({
    selector: 'app-dictionary-entry-editing-cell',
    templateUrl: './dictionary-entry-editing-cell.component.html',
})
export class DictionaryEntryEditingCellComponent {

    private _coordinate: DictionaryEntryEditorCoordinate;

    constructor() {
    }

    @Input()
    get coordinate(): DictionaryEntryEditorCoordinate {
        return this._coordinate;
    }

    set coordinate(value: DictionaryEntryEditorCoordinate) {
        this._coordinate = value;
    }
}
