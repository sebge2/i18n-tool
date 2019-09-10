import {Component, Input, OnInit} from '@angular/core';
import {BundleKeyTranslation} from "../../../model/edition/bundle-key-translation.model";
import {FormGroup} from "@angular/forms";

@Component({
    selector: 'app-translation-editing-cell',
    templateUrl: './translation-editing-cell.component.html',
    styleUrls: ['./translation-editing-cell.component.css']
})
export class TranslationEditingCellComponent implements OnInit {

    @Input()
    formGroup: FormGroup;

    editionStyle: any = {};

    constructor() {
    }

    ngOnInit() {
        this.update(<BundleKeyTranslation>this.formGroup.value.translation);

        this.formGroup.valueChanges.subscribe(
            (formGroup: FormGroup) => {
                this.editionStyle = {'border-color': 'red'};
            }
        )
    }

    private update(translation: BundleKeyTranslation) {
        this.editionStyle = this.updateEditionStyle(translation);
    }

    private updateEditionStyle(translation: BundleKeyTranslation): any {
        if (translation.updatedValue) {
            return {'border-color': 'red'};
            // return {'border-color': 'red', 'animation': 'editing .8s steps(100) infinite'};
        } else {
            return {'border-color': 'transparent'};
        }
    }
}
