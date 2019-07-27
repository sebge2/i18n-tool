import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {Locale} from "../../../model/locale.model";
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

    selectedLocales: Locale[] = [];
    allLocales: Locale[] = [Locale.FR, Locale.NL, Locale.EN]; // TODO
    availableLocales: Locale[] = this.allLocales.slice();
    filteredLocales: Observable<string[]>;

    localeInputCtrl = new FormControl();
    separatorKeysCodes: number[] = [ENTER, COMMA];

    @ViewChild('localeInput', {static: false}) localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) matAutocomplete;

    constructor() {
        this.filteredLocales = this.localeInputCtrl.valueChanges
            .pipe(
                startWith(null),
                map((localeString: string | null) => {
                    return localeString ? this.filter(localeString) : this.availableLocales;
                })
            );
    }

    ngOnInit() {
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        const selectedLocale : Locale = <Locale>event.option.viewValue;

        this.selectedLocales.push(selectedLocale);
        this.localeInput.nativeElement.value = '';
        this.localeInputCtrl.setValue(null);
        this.availableLocales.splice(this.availableLocales.indexOf(selectedLocale), 1);
    }

    add(event: MatChipInputEvent): void {
        if (!this.matAutocomplete.isOpen) {
            const input = event.input;
            const value = event.value;

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
        }
    }

    private filter(value: string): string[] {
        const filterValue = value.toLowerCase();

        return this.availableLocales.filter(locale => locale.toLowerCase().indexOf(filterValue) === 0);
    }
}
