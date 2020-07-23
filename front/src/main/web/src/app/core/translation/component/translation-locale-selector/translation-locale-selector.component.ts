import {
    AfterViewInit,
    Component,
    DoCheck,
    ElementRef,
    forwardRef,
    HostBinding,
    Injector,
    Input,
    OnDestroy,
    OnInit,
    ViewChild
} from '@angular/core';
import {combineLatest, Observable, Subject} from "rxjs";
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {COMMA, ENTER} from "@angular/cdk/keycodes";
import {TranslationLocaleService} from "../../../../translations/service/translation-locale.service";
import {MatChipInputEvent} from "@angular/material/chips";
import {MatAutocomplete, MatAutocompleteSelectedEvent} from "@angular/material/autocomplete";
import {TranslationLocale} from "../../../../translations/model/translation-locale.model";
import {map, startWith, takeUntil} from "rxjs/operators";
import * as _ from "lodash";
import {MatFormFieldControl} from "@angular/material/form-field";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import {FocusMonitor} from '@angular/cdk/a11y';

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
        {provide: MatFormFieldControl, useExisting: TranslationLocaleSelectorComponent}
    ],
})
// https://itnext.io/creating-a-custom-form-field-control-compatible-with-reactive-forms-and-angular-material-cf195905b451
// https://material.angular.io/guide/creating-a-custom-form-field-control
export class TranslationLocaleSelectorComponent implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<TranslationLocale[]> {

    @Input() public labelKey: string = 'SHARED.LOCALES_LABEL';

    @ViewChild('auto', {static: false}) public matAutocomplete: MatAutocomplete;
    @HostBinding('attr.aria-describedby') public describedBy = '';
    @HostBinding() public id = `app-translation-locale-selector-${TranslationLocaleSelectorComponent.nextId++}`;

    public stateChanges = new Subject<void>();
    public focused = false;
    public controlType = 'translation-locale-selector';
    public errorState = false;
    public ngControl: NgControl;

    public readonly parts: FormGroup;
    public filteredLocales$: Observable<TranslationLocale[]>;
    public readonly separatorKeysCodes: number[] = [ENTER, COMMA];

    private static nextId = 0;

    private _remainingAvailableLocales$: Observable<TranslationLocale[]>;

    private onChange = (_: any) => {};
    private onTouched = () => {};
    private _placeholder: string;
    private _disabled = false;
    private _required = false;
    private readonly _destroyed$ = new Subject();

    constructor(private injector: Injector,
                private focusMonitor: FocusMonitor,
                private formBuilder: FormBuilder,
                private elRef: ElementRef<HTMLElement>,
                private _localeService: TranslationLocaleService) {
        this.parts = formBuilder.group({
            input: [null],
            list: [[]]
        });
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

        this.filteredLocales$ = combineLatest([this.parts.controls['input'].valueChanges.pipe(startWith(<string>null)), this._remainingAvailableLocales$])
            .pipe(
                takeUntil(this._destroyed$),
                map(([locale, remainingAvailableLocales]) => locale ? this.filterRemaining(locale, remainingAvailableLocales) : remainingAvailableLocales)
            );
    }

    ngAfterViewInit(): void {
        this.focusMonitor
            .monitor(this.elRef.nativeElement, true)
            .subscribe(origin => {
                this.focused = !!origin;
                this.stateChanges.next();
            });
    }

    ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();

        this.focusMonitor.stopMonitoring(this.elRef.nativeElement);
        this.stateChanges.complete();
    }

    ngDoCheck(): void {
        if (this.ngControl) {
            this.errorState = this.ngControl.invalid && this.ngControl.touched;
            this.stateChanges.next();
        }
    }

    @Input()
    get placeholder(): string {
        return this._placeholder;
    }

    set placeholder(value: string) {
        this._placeholder = value;
        this.stateChanges.next();
    }

    @Input()
    get disabled(): boolean {
        return this._disabled;
    }

    set disabled(value: boolean) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.parts.disable() : this.parts.enable();
        this.stateChanges.next();
    }

    @Input()
    get required() {
        return this._required;
    }

    set required(req) {
        this._required = coerceBooleanProperty(req);
        this.stateChanges.next();
    }

    get value(): TranslationLocale[] {
        return this.parts.controls['list'].value;
    }

    set value(value: TranslationLocale[]) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    get empty() {
        return _.isEmpty(this.parts.controls['list']);
    }

    @HostBinding('class.floating')
    get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'input') {
            this.elRef.nativeElement.querySelector('input').focus();
        }
    }

    writeValue(values: TranslationLocale[] | null): void {
        this.parts.controls['list'].setValue(values);
    }

    registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    selected(event: MatAutocompleteSelectedEvent): void {
        this.add(<TranslationLocale><unknown>event.option.value);
    }

    add(selectedLocale: TranslationLocale) {
        this.parts.controls['input'].setValue(null);

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
        }
    }

    private setValue(value: TranslationLocale[]) {
        this.value = value;
        this.stateChanges.next();
    }

    private getIndex(locale: TranslationLocale, locales: TranslationLocale[]) {
        return _.findIndex(locales, first => locale.equals(first));
    }

    private filterOutPresent(localesToFilter: TranslationLocale[], others: TranslationLocale[]): TranslationLocale[] {
        return localesToFilter.filter(locale => this.getIndex(locale, others) < 0);
    }

    private filterRemaining(value: string | TranslationLocale, remainingAvailableLocales: TranslationLocale[]): TranslationLocale[] {
        if (!value) {
            return remainingAvailableLocales;
        } else if (value instanceof TranslationLocale) {
            return remainingAvailableLocales.filter(locale => !locale.equals(value));
        } else {
            const filterValue = value.toLowerCase();

            return remainingAvailableLocales.filter(locale => locale.displayName.toLowerCase().indexOf(filterValue) === 0);
        }
    }

}
