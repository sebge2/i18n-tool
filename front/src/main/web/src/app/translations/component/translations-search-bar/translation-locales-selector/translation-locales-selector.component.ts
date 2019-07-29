import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../../model/locale.model";
import {MatAutocompleteSelectedEvent, MatChipInputEvent} from "@angular/material";
import {FormControl} from "@angular/forms";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith} from "rxjs/operators";
import {Observable} from "rxjs";

@Component({
    selector: 'app-translation-locales-selector',
    templateUrl: './translation-locales-selector.component.html',
    styleUrls: ['./translation-locales-selector.component.css']
})
export class TranslationLocalesSelectorComponent implements OnInit {

    selectedLocales: Locale[] = [Locale.FR, Locale.EN];
    allLocales: Locale[] = ALL_LOCALES;
    availableLocales: Locale[];
    filteredLocales: Observable<string[]>;

    localeInputCtrl = new FormControl();
    separatorKeysCodes: number[] = [ENTER, COMMA];

    @ViewChild('localeInput', {static: false}) localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) matAutocomplete;

    @Output('selectedLocales')
    emitter: EventEmitter<Locale[]> = new EventEmitter();

    constructor() {
    }

    ngOnInit() {
        this.filteredLocales = this.localeInputCtrl.valueChanges
            .pipe(
                startWith(null),
                map((localeString: string | null) => {
                    return localeString ? this.filter(localeString) : this.availableLocales;
                })
            );

        this.availableLocales = this.allLocales.filter(locale => this.selectedLocales.indexOf(locale) < 0);
        this.emitter.emit(this.selectedLocales)
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        const selectedLocale: Locale = <Locale>event.option.viewValue;

        this.localeInput.nativeElement.value = '';
        this.localeInputCtrl.setValue(null);

        this.selectedLocales.push(selectedLocale);
        this.availableLocales.splice(this.availableLocales.indexOf(selectedLocale), 1);

        this.emitter.emit(this.selectedLocales);
    }

    add(event: MatChipInputEvent): void {
        if (!this.matAutocomplete.isOpen) {
            const input = event.input;

            if (input) {
                input.value = '';
            }

            this.localeInputCtrl.setValue(null);
        }
    }

    remove(locale: Locale): void {
        const index = this.selectedLocales.indexOf(locale);

        if (index >= 0) {
            this.selectedLocales.splice(index, 1);
            this.availableLocales.push(locale);
            this.emitter.emit(this.selectedLocales);
        }
    }

    private filter(value: string): string[] {
        const filterValue = value.toLowerCase();

        return this.availableLocales.filter(locale => locale.toLowerCase().indexOf(filterValue) === 0);
    }
}
