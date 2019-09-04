import {Component, ElementRef, EventEmitter, OnInit, Output, ViewChild, Input} from '@angular/core';
import {ALL_LOCALES, Locale} from "../../../model/locale.model";
import {MatAutocompleteSelectedEvent, MatChipInputEvent} from "@angular/material";
import {FormControl} from "@angular/forms";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith} from "rxjs/operators";
import {Observable} from 'rxjs';
import {UserSettingsService} from "../../../../settings/service/user-settings.service";

@Component({
    selector: 'app-translation-locales-selector',
    templateUrl: './translation-locales-selector.component.html',
    styleUrls: ['./translation-locales-selector.component.css']
})
export class TranslationLocalesSelectorComponent implements OnInit {

    @ViewChild('localeInput', {static: false}) localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) matAutocomplete;

    @Output()
    valueChange: EventEmitter<Locale[]> = new EventEmitter();

    value: Locale[] = [];

    allLocales: Locale[] = ALL_LOCALES;
    availableLocales: Locale[];
    filteredLocales: Observable<string[]>;

    localeInputCtrl = new FormControl();
    separatorKeysCodes: number[] = [ENTER, COMMA];

    constructor(private userSettingsService: UserSettingsService) {
        this.value = userSettingsService.getUserLocales();
    }

    ngOnInit() {
        this.filteredLocales = this.localeInputCtrl.valueChanges
            .pipe(
                startWith(null),
                map((localeString: string | null) => {
                    return localeString ? this.filter(localeString) : this.availableLocales;
                })
            );

        this.availableLocales = this.allLocales.filter(locale => this.value.indexOf(locale) < 0);
        this.valueChange.emit(this.value.slice())
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        const selectedLocale: Locale = <Locale>event.option.viewValue;

        this.localeInput.nativeElement.value = '';
        this.localeInputCtrl.setValue(null);

        this.value.push(selectedLocale);
        this.availableLocales.splice(this.availableLocales.indexOf(selectedLocale), 1);

        this.valueChange.emit(this.value.slice());
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
        const index = this.value.indexOf(locale);

        if (index >= 0) {
            this.value.splice(index, 1);
            this.availableLocales.push(locale);
            this.valueChange.emit(this.value.slice());
        }
    }

    private filter(value: string): string[] {
        const filterValue = value.toLowerCase();

        return this.availableLocales.filter(locale => locale.toLowerCase().indexOf(filterValue) === 0);
    }
}
