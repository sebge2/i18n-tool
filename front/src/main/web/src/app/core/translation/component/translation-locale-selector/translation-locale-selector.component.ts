import {Component, ElementRef, EventEmitter, forwardRef, Input, OnInit, Output, ViewChild} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {ControlValueAccessor, DefaultValueAccessor, FormControl, NG_VALUE_ACCESSOR} from "@angular/forms";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {map, startWith, takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {MatFormFieldControl} from "@angular/material/form-field";

@Component({
    selector: 'app-translation-locale-selector',
    templateUrl: './translation-locale-selector.component.html',
    styleUrls: ['./translation-locale-selector.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TranslationLocaleSelectorComponent),
            multi: true
        },
        // {provide: MatFormFieldControl, useExisting: TranslationLocaleSelectorComponent}
    ]
})
export class TranslationLocaleSelectorComponent implements OnInit/*, ControlValueAccessor*/ {

    @Input() public value: TranslationLocale[] = [];
    @Output() public readonly valueChange: EventEmitter<TranslationLocale[]> = new EventEmitter();

    @Input() public labelKey: string = 'SHARED.LOCALES_LABEL';

    // @ViewChild('localesList', {static: false}) public valueAccessor: DefaultValueAccessor;
    @ViewChild('localeInput', {static: false}) public localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) public matAutocomplete;

    public filteredLocales$: Observable<TranslationLocale[]>;
    public readonly localeInputCtrl = new FormControl();
    public readonly separatorKeysCodes: number[] = [ENTER, COMMA];

    private static nextId = 0;

    private _remainingAvailableLocales$: Observable<TranslationLocale[]>;

    private onChange = (_: any) => {};
    private onTouched = () => {};
    private _placeholder: string;
    private _disabled = false;
    private _required = false;
    private readonly _destroyed$ = new Subject();

    constructor(private _localeService: TranslationLocaleService) {
    }

    ngOnInit() {
        this.ngControl = this.injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }

        this._remainingAvailableLocales$ = combineLatest([this._localeService.getAvailableLocales(), this.parts.valueChanges.pipe(startWith(<string>null))])
            .pipe(
                takeUntil(this._destroyed$),
                map(([availableLocales, _]) => this.filterOutPresent(availableLocales, this.value))
            );

        this.filteredLocales$ = combineLatest([this.localeInputCtrl.valueChanges.pipe(startWith(<string>null)), this._remainingAvailableLocales$])
            .pipe(
                takeUntil(this._destroyed$),
                map(([locale, remainingAvailableLocales]) => locale ? this.filterRemaining(locale, remainingAvailableLocales) : remainingAvailableLocales)
            );

        this.valueChange.emit(this.value.slice());
    }

    ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    // writeValue(obj: any) {
    //     this.valueAccessor.writeValue(obj);
    // }
    //
    // registerOnChange(fn: any) {
    //     this.valueAccessor.registerOnChange(fn);
    // }
    //
    // registerOnTouched(fn: any) {
    //     this.valueAccessor.registerOnTouched(fn);
    // }
    //
    // setDisabledState(isDisabled: boolean) {
    //     this.valueAccessor.setDisabledState(isDisabled);
    // }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.add(<TranslationLocale><unknown>event.option.value);
    }

    add(selectedLocale: TranslationLocale) {
        this.localeInput.nativeElement.value = '';
        this.localeInputCtrl.setValue(null);

        const copy = _.clone(this.value);
        copy.push(selectedLocale);

        this.setValue(copy);
    }

    remove(locale: TranslationLocale): void {
        const index = this.getIndex(locale, this.value);

        if (index >= 0) {
            const copy = _.clone(this.value);
            copy.splice(index, 1);

            this.setValue(copy);
        }
    }

    onTokenEnd(event: MatChipInputEvent): void {
        if (!this.matAutocomplete.isOpen) {
            const input = event.input;

            if (input) {
                input.value = '';
            }

            this.localeInputCtrl.setValue(null);
        }
    }

    private getIndex(locale: TranslationLocale, locales: TranslationLocale[]) {
        return _.findIndex(locales, first => locale.equals(first));
    }

    private filterOutPresent(localesToFilter: TranslationLocale[], others: TranslationLocale[]): TranslationLocale[] {
        return localesToFilter.filter(locale => this.getIndex(locale, others) < 0);
    }

    private filterRemaining(value: string | TranslationLocale, remainingAvailableLocales: TranslationLocale[]): TranslationLocale[] {
        if(!value){
            return remainingAvailableLocales;
        } else if(value instanceof TranslationLocale){
            return remainingAvailableLocales.filter(locale => !locale.equals(value));
        } else {
            const filterValue = value.toLowerCase();

            return remainingAvailableLocales.filter(locale => locale.displayName.toLowerCase().indexOf(filterValue) === 0);
        }
    }

}
