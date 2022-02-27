import {Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {CardSelectorItem, getStringValue, StepChangeEvent, WizardComponent} from "@i18n-core-shared";
import {FormArray, FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Observable, Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {ExternalTranslatorConfig, ExternalTranslatorService} from "@i18n-dictionary";

export enum ExternalTranslatorSelection {

    GOOGLE = 'GOOGLE',

    AZURE = 'AZURE',

    ITRANSLATE = 'ITRANSLATE',

    REST_API = 'REST_API',
}

export interface TranslatorCreator {

    create(service: ExternalTranslatorService): Observable<ExternalTranslatorConfig>;
}

@Component({
    selector: 'app-external-translator-add-popup',
    templateUrl: './external-translator-add-popup.component.html',
})
export class ExternalTranslatorAddPopupComponent implements OnInit, OnDestroy {

    private static STEP_SETUP = 1;
    private static STEP_RESULT = 2;

    private static FORM_STEP_SELECTION = 0;
    private static FORM_STEP_NO_TYPE_CONFIG = 1;
    private static FORM_STEP_AZURE_CONFIG = 2;
    private static FORM_STEP_GOOGLE_CONFIG = 3;
    private static FORM_STEP_ITRANSLATE_CONFIG = 4;
    private static FORM_STEP_REST_API_CONFIG = 5;
    private static FORM_STEP_RESULT = 6;

    form: FormGroup;
    selection: ExternalTranslatorSelection;
    translatorCreator: TranslatorCreator;
    createdConfig: ExternalTranslatorConfig;
    readonly availableTemplates: CardSelectorItem[] = [
        {
            id: ExternalTranslatorSelection.GOOGLE,
            icon: 'app-icon-google',
            label: 'Google',
        },
        {
            id: ExternalTranslatorSelection.AZURE,
            icon: 'app-icon-azur',
            label: 'Azure',
        },
        {
            id: ExternalTranslatorSelection.ITRANSLATE,
            icon: 'app-icon-itranslate-bw',
            label: 'iTranslate',
         },
        // TODO
        // {
        //     id: ExternalTranslatorSelection.REST_API,
        //     icon: 'app-icon-rest-api',
        //     label: 'REST API',
        // }
    ];


    @ViewChild('wizard', {static: true}) private wizard: WizardComponent;

    private readonly _destroyed$ = new Subject<void>();

    constructor(private formBuilder: FormBuilder) {
        this.form = this.formBuilder.group({
            stepsForm: this.formBuilder.array([
                this.formBuilder.group({type: this.formBuilder.control('', [Validators.required])}), // step selection

                this.formBuilder.group({}), // step config no type selected

                this.formBuilder.group({
                    subscriptionKey: this.formBuilder.control('', [Validators.required]),
                    subscriptionRegion: this.formBuilder.control('', [Validators.required]),
                }), // step config azure

                this.formBuilder.group({
                    apiKey: this.formBuilder.control('', [Validators.required]),
                }), // step config google

                this.formBuilder.group({
                    bearerToken: this.formBuilder.control('', [Validators.required]),
                }), // step config iTranslate

                this.formBuilder.group({}), // step config REST API

                this.formBuilder.group({
                    config: this.formBuilder.control(null, [Validators.required]),
                }), // step result
            ]),
        });
    }

    ngOnInit(): void {
        this.stepSelectionForm.statusChanges.pipe(takeUntil(this._destroyed$)).subscribe((_) => {
            if (this.stepSelectionForm.valid) {
                this.wizard.nextStep();
            }
        });
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();
    }

    get stepSelectionEditable(): boolean {
        // we stay in the selection step until something is selected, once selected we cannot come back
        return !this.selection;
    }

    get stepSelectionForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(ExternalTranslatorAddPopupComponent.FORM_STEP_SELECTION);
    }

    get stepConfigForm(): FormGroup {
        let index;
        if (!this.selection) {
            index = ExternalTranslatorAddPopupComponent.FORM_STEP_NO_TYPE_CONFIG;
        } else if (this.selection === ExternalTranslatorSelection.AZURE) {
            index = ExternalTranslatorAddPopupComponent.FORM_STEP_AZURE_CONFIG;
        } else if (this.selection === ExternalTranslatorSelection.GOOGLE) {
            index = ExternalTranslatorAddPopupComponent.FORM_STEP_GOOGLE_CONFIG;
        } else if (this.selection === ExternalTranslatorSelection.ITRANSLATE) {
            index = ExternalTranslatorAddPopupComponent.FORM_STEP_ITRANSLATE_CONFIG;
        } else if (this.selection === ExternalTranslatorSelection.REST_API) {
            index = ExternalTranslatorAddPopupComponent.FORM_STEP_REST_API_CONFIG;
        }

        return <FormGroup>this.stepsForm.at(index);
    }

    get stepConfigEditable(): boolean {
        // it's still editable until the translator has not been properly created
        return this.stepResultEditable;
    }

    get stepResultForm(): FormGroup {
        return <FormGroup>this.stepsForm.at(ExternalTranslatorAddPopupComponent.FORM_STEP_RESULT);
    }

    get stepResultEditable(): boolean {
        return !this.stepResultForm.valid;
    }

    onStepChange(stepChangeEvent: StepChangeEvent) {
        if (stepChangeEvent.nextStepIndex == ExternalTranslatorAddPopupComponent.STEP_SETUP) {
            this.selection = this.stepSelectionForm.controls['type'].value;
        } else if (stepChangeEvent.nextStepIndex == ExternalTranslatorAddPopupComponent.STEP_RESULT) {
            this.translatorCreator = this.createRequest();
        }
    }

    private createRequest(): TranslatorCreator | null {
        if (!this.selection) {
            return null;
        } else if (this.selection == ExternalTranslatorSelection.AZURE) {
            const subscriptionKey = getStringValue(this.stepConfigForm.controls['subscriptionKey']);
            const subscriptionRegion = getStringValue(this.stepConfigForm.controls['subscriptionRegion']);

            return <TranslatorCreator>{
                create(service: ExternalTranslatorService): Observable<ExternalTranslatorConfig> {
                    return service.createAzureConfig$(subscriptionKey, subscriptionRegion)
                }
            };
        } else if (this.selection == ExternalTranslatorSelection.GOOGLE) {
            const apiKey = getStringValue(this.stepConfigForm.controls['apiKey']);

            return <TranslatorCreator>{
                create(service: ExternalTranslatorService): Observable<ExternalTranslatorConfig> {
                    return service.createGoogleConfig$(apiKey);
                }
            };
        } else if (this.selection == ExternalTranslatorSelection.ITRANSLATE) {
            const bearerToken = getStringValue(this.stepConfigForm.controls['bearerToken']);

            return <TranslatorCreator>{
                create(service: ExternalTranslatorService): Observable<ExternalTranslatorConfig> {
                    return service.createITranslateConfig$(bearerToken);
                }
            };
        } else if (this.selection == ExternalTranslatorSelection.REST_API) {
            // TODO support generic REST API
            // return <TranslatorCreator>{
            //     create(service: ExternalTranslatorService): Observable<ExternalTranslatorConfig> {
            //         return null;
            //         // return service.createConfig$({
            //         //     label: getStringValue(this.stepConfigForm.controls['label'].value),
            //         //     linkUrl: getStringValue(this.stepConfigForm.controls['linkUrl'].value),
            //         //     method: this.stepConfigForm.controls['method'].value,
            //         //     url: getStringValue(this.stepConfigForm.controls['url'].value),
            //         //     queryHeaders: /*this.stepConfigForm.controls['queryHeaders'].value*/  null,
            //         //     queryParameters: /*this.stepConfigForm.controls['queryParameters'].value*/ null,
            //         //     bodyTemplate: this.stepConfigForm.controls['bodyTemplate'].value,
            //         //     queryExtractor: getStringValue(this.stepConfigForm.controls['queryExtractor'].value),
            //         // });
            //     }
            // };
        } else {
            throw Error(`Unsupported selection type ${this.selection}.`);
        }
    }

    private get stepsForm(): FormArray | null {
        return <FormArray>this.form.controls['stepsForm'];
    }
}
