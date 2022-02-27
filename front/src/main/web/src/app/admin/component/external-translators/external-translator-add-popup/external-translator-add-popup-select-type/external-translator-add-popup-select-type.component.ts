import {Component, Input} from '@angular/core';
import {CardSelectorItem} from "@i18n-core-shared";
import {ExternalTranslatorSelection} from "../external-translator-add-popup.component";
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-external-translator-add-popup-select-type',
    templateUrl: './external-translator-add-popup-select-type.component.html',
})
export class ExternalTranslatorAddPopupSelectTypeComponent {

    @Input() form: FormGroup;
    @Input() availableTemplates: CardSelectorItem[] = [];

    constructor() {
    }

    onSelect(selection: ExternalTranslatorSelection) {
        this.form.controls['type'].setValue(selection);
    }

}
