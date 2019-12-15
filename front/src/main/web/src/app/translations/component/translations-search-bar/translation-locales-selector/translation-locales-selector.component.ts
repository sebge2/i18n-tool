import {Component, ElementRef, EventEmitter, OnDestroy, OnInit, Output, ViewChild} from '@angular/core';
import {Locale} from "../../../model/locale.model";
import {MatAutocompleteSelectedEvent, MatChipInputEvent} from "@angular/material";
import {FormControl} from "@angular/forms";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {map, startWith, take, takeUntil} from "rxjs/operators";
import {Observable, Subject} from 'rxjs';
import {UserSettingsService} from "../../../../settings/service/user-settings.service";
import {LocalesTranslationsService} from "../../../service/locales-translations.service";

@Component({
    selector: 'app-translation-locales-selector',
    templateUrl: './translation-locales-selector.component.html',
    styleUrls: ['./translation-locales-selector.component.css']
})
export class TranslationLocalesSelectorComponent implements OnInit, OnDestroy {

    @ViewChild('localeInput', {static: false}) localeInput: ElementRef<HTMLInputElement>;
    @ViewChild('auto', {static: false}) matAutocomplete;

    @Output()
    valueChange: EventEmitter<Locale[]> = new EventEmitter();

    value: Locale[] = [];
    allLocales: Locale[];
    availableLocales: Locale[];
    filteredLocales: Observable<Locale[]>;

    localeInputCtrl = new FormControl();
    separatorKeysCodes: number[] = [ENTER, COMMA];

    private destroy$ = new Subject();

    constructor(private userSettingsService: UserSettingsService,
                private localeService: LocalesTranslationsService) {
        this.allLocales = localeService.getAvailableLocales();
    }

    ngOnInit() {
        this.userSettingsService
            .getUserLocales()
            .pipe(
                takeUntil(this.destroy$),
                take(1)
            )
            .subscribe(locales => this.value = locales);

        this.filteredLocales = this.localeInputCtrl.valueChanges
            .pipe(
                takeUntil(this.destroy$),
                startWith(null),
                map((localeString: string | null) => {
                    return localeString ? this.filter(localeString) : this.availableLocales;
                })
            );

        this.availableLocales = this.allLocales.filter(locale => this.value.indexOf(locale) < 0);
        this.valueChange.emit(this.value.slice())
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        const selectedLocale: Locale = <Locale><unknown>event.option.value;

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

    private filter(value: string): Locale[] {
        const filterValue = value.toLowerCase();

        return this.availableLocales.filter(locale => locale.toString().toLowerCase().indexOf(filterValue) === 0);
    }
}
