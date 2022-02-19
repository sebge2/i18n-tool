import {Component, Input} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {ExternalTranslatorSelection} from "../external-translator-add-popup.component";

@Component({
    selector: 'app-external-translator-add-popup-configure',
    templateUrl: './external-translator-add-popup-configure.component.html',
})
export class ExternalTranslatorAddPopupConfigureComponent {

    @Input() form: FormGroup;
    @Input() selection: ExternalTranslatorSelection;

    ExternalTranslatorSelection = ExternalTranslatorSelection;

    constructor() {
    }

}
