import {Component, ElementRef, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import {FormControl} from "@angular/forms";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {map, startWith, takeUntil} from "rxjs/operators";
import * as _ from "lodash";

@Component({
    selector: 'app-translation-locale-selector',
    templateUrl: './translation-locale-selector.component.html',
    styleUrls: ['./translation-locale-selector.component.css'],
})
export class TranslationLocaleSelectorComponent implements OnInit {

    @Input() public value: TranslationLocale[] = [];
    @Output() public valueChange: EventEmitter<TranslationLocale[]> = new EventEmitter();

    @Input() public labelKey: string = 'SHARED.LOCALES_LABEL';

    @ViewChild('localeInput', {static: false}) private localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) private matAutocomplete;

    public filteredLocales$: Observable<TranslationLocale[]>;
    public localeInputCtrl = new FormControl();
    public separatorKeysCodes: number[] = [ENTER, COMMA];

    private _remainingAvailableLocales$ = new BehaviorSubject<TranslationLocale[]>([]);
    private readonly _destroyed$ = new Subject();

    constructor(private _localeService: TranslationLocaleService) {
    }

    ngOnInit() {
        this._localeService
            .getDefaultLocales()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(availableLocales => {
                this.value = this.filterOutUnknown(this.value, availableLocales);
                this.valueChange.emit(this.value.slice());

                this._remainingAvailableLocales$.next(this.filterOutPresent(availableLocales, this.value));
            });

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

    selected(event: MatAutocompleteSelectedEvent): void {
        this.add(<TranslationLocale><unknown>event.option.value);
    }

    add(selectedLocale: TranslationLocale) {
        this.localeInput.nativeElement.value = '';
        this.localeInputCtrl.setValue(null);

        this.value.push(selectedLocale);

        const remainingAvailableLocales = this._remainingAvailableLocales$.getValue();
        remainingAvailableLocales.splice(this.getIndex(selectedLocale, remainingAvailableLocales), 1);

        this.valueChange.emit(this.value.slice());
    }

    remove(locale: TranslationLocale): void {
        const index = this.getIndex(locale, this.value);

        if (index >= 0) {
            this.value.splice(index, 1);

            const remainingAvailableLocales = this._remainingAvailableLocales$.getValue();
            remainingAvailableLocales.push(locale);

            this._remainingAvailableLocales$.next(remainingAvailableLocales);
            this.valueChange.emit(this.value.slice());
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

    private filterOutUnknown(localesToFilter: TranslationLocale[], availableLocales: TranslationLocale[]): TranslationLocale[] {
        return localesToFilter.filter(locale => this.getIndex(locale, availableLocales) >= 0);
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
