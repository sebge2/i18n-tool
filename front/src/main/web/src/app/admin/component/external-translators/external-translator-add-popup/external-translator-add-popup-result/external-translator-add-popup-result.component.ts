import {Component, Input, OnDestroy} from '@angular/core';
import {FormGroup} from "@angular/forms";
import {Subject} from "rxjs";
import {ExternalTranslatorConfig, ExternalTranslatorService} from "@i18n-dictionary";
import {takeUntil} from "rxjs/operators";
import {TranslatorCreator} from "../external-translator-add-popup.component";

@Component({
    selector: 'app-external-translator-add-popup-result',
    templateUrl: './external-translator-add-popup-result.component.html',
    styleUrls: ['./external-translator-add-popup-result.component.scss']
})
export class ExternalTranslatorAddPopupResultComponent implements OnDestroy {

    @Input() form: FormGroup;

    creationInProgress = false;
    error: any;

    private _translatorCreator: TranslatorCreator;
    private readonly _destroyed$ = new Subject<void>();

    constructor(private _translatorService: ExternalTranslatorService) {
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    @Input()
    get translatorCreator(): TranslatorCreator {
        return this._translatorCreator;
    }

    set translatorCreator(value: TranslatorCreator) {
        this._translatorCreator = value;
        this.translator = null;
        this.error = null;
        this.creationInProgress = false;

        if (this._translatorCreator) {
            this.creationInProgress = true;
            this.translatorCreator
                .create(this._translatorService)
                .pipe(takeUntil(this._destroyed$))
                .toPromise()
                .then((translator) => (this.translator = translator))
                .catch((error) => this.error = error)
                .finally(() => (this.creationInProgress = false));
        }
    }

    get translator(): ExternalTranslatorConfig {
        return this.form.controls['config'].value;
    }

    set translator(value: ExternalTranslatorConfig) {
        this.form.controls['config'].setValue(value);
    }
}
