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
    OnInit
} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {MatFormFieldControl} from "@angular/material/form-field";
import {TranslationLocale, TranslationLocaleService} from "@i18n-core-translation";
import {Subject} from "rxjs";
import {FocusMonitor} from "@angular/cdk/a11y";
import {takeUntil} from "rxjs/operators";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import * as _ from "lodash";

@Component({
    selector: 'app-translation-locale-single-selector',
    templateUrl: './translation-locale-single-selector.component.html',
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TranslationLocaleSingleSelectorComponent),
            multi: true,
        },
        {provide: MatFormFieldControl, useExisting: TranslationLocaleSingleSelectorComponent},
    ],
})
export class TranslationLocaleSingleSelectorComponent
    implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<TranslationLocale> {
    @Input() labelKey: string = '';

    @HostBinding('attr.aria-describedby') describedBy = '';
    @HostBinding() id = `app-translation-locale-single-selector-${TranslationLocaleSingleSelectorComponent.nextId++}`;

    stateChanges = new Subject<void>();
    focused = false;
    controlType = 'translation-locale-single-selector';
    errorState = false;
    ngControl: NgControl;

    readonly parts: FormGroup;
    allLocales: TranslationLocale[] = [];

    private static nextId = 0;

    private onChange = (_: any) => {
    };
    private onTouched = () => {
    };
    private _placeholder: string;
    private _disabled = false;
    private _required = false;
    private readonly _destroyed$ = new Subject<void>();

    constructor(
        private _injector: Injector,
        private _focusMonitor: FocusMonitor,
        private _formBuilder: FormBuilder,
        private _elRef: ElementRef<HTMLElement>,
        public localeService: TranslationLocaleService,
    ) {
        this.parts = _formBuilder.group({
            translationLocale: this._formBuilder.control(null),
        });

        this.parts.controls['translationLocale'].valueChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe((_) => this.onChange(this.value));

        this.localeService.getAvailableLocales()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(availableLocales => this._updateLocales(availableLocales));
    }

    ngOnInit() {
        this.ngControl = this._injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    ngAfterViewInit(): void {
        this._focusMonitor
            .monitor(this._elRef.nativeElement, true)
            .pipe(takeUntil(this._destroyed$))
            .subscribe((origin) => {
                this.focused = !!origin;
                this.stateChanges.next();
            });
    }

    ngOnDestroy(): void {
        this._destroyed$.next(null);
        this._destroyed$.complete();

        this._focusMonitor.stopMonitoring(this._elRef.nativeElement);
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

    get value(): TranslationLocale {
        return this.parts.controls['translationLocale'].value;
    }

    set value(value: TranslationLocale) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    get empty() {
        return _.isEmpty(this.parts.controls['translationLocale']);
    }

    @HostBinding('class.floating')
    get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'mat-select') {
            (<HTMLElement>this._elRef.nativeElement.querySelector('mat-select')).focus();
        }
    }

    writeValue(value: TranslationLocale | null): void {
        this.parts.controls['translationLocale'].setValue(
            this._isLocaleRegistered(value)
                ? value
                : null
        );
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

    compareIds(locale1: TranslationLocale, locale2: TranslationLocale): boolean {
        return _.eq(
            _.get(locale1, 'id'),
            _.get(locale2, 'id')
        );
    }

    private _updateLocales(availableLocales: TranslationLocale[]): void {
        this.allLocales = availableLocales;

        if (!_.isNil(this.value) && !this._isLocaleRegistered(this.value)) {
            this._setValue(null);
        }
    }

    private _isLocaleRegistered(value: TranslationLocale): boolean {
        return _.some(this.allLocales, locale => locale.equals(value));
    }

    private _setValue(value: TranslationLocale): void {
        this.value = value;
        this.stateChanges.next();
    }
}
