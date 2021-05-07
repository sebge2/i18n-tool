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
    TemplateRef,
    ViewChild
} from '@angular/core';
import {TranslationStringPatternStrategy} from "../../../model/search/translation-string-pattern-strategy.enum";
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {MatFormFieldControl} from "@angular/material/form-field";
import {TranslationKeyPattern} from "../../../model/search/translation-key-pattern.model";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FocusMonitor} from "@angular/cdk/a11y";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import * as _ from "lodash";

@Component({
    selector: 'app-translation-string-pattern-input',
    templateUrl: './translation-string-pattern-input.component.html',
    styleUrls: ['./translation-string-pattern-input.component.scss'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TranslationStringPatternInputComponent),
            multi: true
        },
        {provide: MatFormFieldControl, useExisting: TranslationStringPatternInputComponent}
    ],
})
export class TranslationStringPatternInputComponent implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<TranslationKeyPattern> {

    @ViewChild(`${TranslationStringPatternStrategy.STARTS_WITH}`) public startsWithTemplate: TemplateRef<any>;
    @ViewChild(`${TranslationStringPatternStrategy.CONTAINS}`) public containsTemplate: TemplateRef<any>;
    @ViewChild(`${TranslationStringPatternStrategy.EQUALS}`) public equalsTemplate: TemplateRef<any>;
    @ViewChild(`${TranslationStringPatternStrategy.ENDS_WITH}`) public endsWithTemplate: TemplateRef<any>;

    @HostBinding('attr.aria-describedby') public describedBy = '';
    @HostBinding() public id = `app-translation-string-pattern-input-${TranslationStringPatternInputComponent.nextId++}`;

    public stateChanges = new Subject<void>();
    public focused = false;
    public controlType = 'app-translation-string-pattern-input';
    public errorState = false;
    public ngControl: NgControl;

    public readonly parts: FormGroup;
    public strategyTemplates: TemplateRef<any>[] = [];

    private static nextId = 0;

    private onChange = (_: any) => {
    };
    private onTouched = () => {
    };
    private _placeholder: string;
    private _disabled = false;
    private _forceDisabled = false;
    private _required = false;

    private readonly _destroyed$ = new Subject();

    private static readonly STRATEGY_ORDER : TranslationStringPatternStrategy[] = [
        TranslationStringPatternStrategy.STARTS_WITH,
        TranslationStringPatternStrategy.CONTAINS,
        TranslationStringPatternStrategy.ENDS_WITH,
        TranslationStringPatternStrategy.EQUALS
    ];

    private readonly strategyMappings = {
        [TranslationStringPatternStrategy.STARTS_WITH] : () => this.startsWithTemplate,
        [TranslationStringPatternStrategy.CONTAINS]: () => this.containsTemplate,
        [TranslationStringPatternStrategy.ENDS_WITH]: () => this.endsWithTemplate,
        [TranslationStringPatternStrategy.EQUALS]: () => this.equalsTemplate,
    };

    constructor(private injector: Injector,
                private focusMonitor: FocusMonitor,
                private formBuilder: FormBuilder,
                private elRef: ElementRef<HTMLElement>,) {
        this.parts = formBuilder.group({
            pattern: [null],
            strategy: [null]
        });
    }

    public ngOnInit(): void {
        this.ngControl = this.injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }

        this.parts
            .valueChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe(_ => this.onChange(this.value));
    }

    public ngAfterViewInit(): void {
        setTimeout(
            () => this.strategyTemplates = _.map(TranslationStringPatternInputComponent.STRATEGY_ORDER, strategy => this.strategyMappings[strategy]()),
            0
        );

        this.focusMonitor
            .monitor(this.elRef.nativeElement, true)
            .pipe(takeUntil(this._destroyed$))
            .subscribe(origin => {
                this.focused = !!origin;
                this.stateChanges.next();
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();

        this.focusMonitor.stopMonitoring(this.elRef.nativeElement);
        this.stateChanges.complete();
    }

    public ngDoCheck(): void {
        if (this.ngControl) {
            this.errorState = this.ngControl.invalid && this.ngControl.touched;
            this.stateChanges.next();
        }
    }

    @Input()
    public get placeholder(): string {
        return this._placeholder;
    }

    public set placeholder(value: string) {
        this._placeholder = value;
        this.stateChanges.next();
    }

    @Input()
    public get disabled(): boolean {
        return this._disabled || this.forceDisabled;
    }

    public set disabled(disabled: boolean) {
        if (this._disabled != disabled) {
            this._disabled = disabled;
            this.updateDisableState();
        }
    }

    public get forceDisabled(): boolean {
        return this._forceDisabled;
    }

    public set forceDisabled(disabled: boolean) {
        if (this._forceDisabled != disabled) {
            this._forceDisabled = disabled;
            this.updateDisableState();
        }
    }

    @Input()
    public get required() {
        return this._required;
    }

    public set required(req) {
        this._required = coerceBooleanProperty(req);
        this.stateChanges.next();
    }

    public get value(): TranslationKeyPattern | undefined {
        if (this.empty) {
            return null;
        }

        return new TranslationKeyPattern(
            this.parts.controls['strategy'].value as TranslationStringPatternStrategy,
            this.parts.controls['pattern'].value as string,
        );
    }

    public set value(value: TranslationKeyPattern | undefined) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    public get selectedStrategy(): TemplateRef<any> {
        return this.strategyTemplates[TranslationStringPatternInputComponent.STRATEGY_ORDER.indexOf(this.parts.controls['strategy'].value)];
    }

    public get empty() {
        return _.isEmpty(this.parts.controls['pattern'].value);
    }

    @HostBinding('class.floating')
    public get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    public setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    public onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'input') {
            this.elRef.nativeElement.querySelector('input').focus();
        }
    }

    public writeValue(value: TranslationKeyPattern | undefined): void {
        this.parts.controls['pattern'].setValue(_.get(value, 'pattern'));
        this.parts.controls['strategy'].setValue(_.get(value, 'strategy', TranslationStringPatternStrategy.CONTAINS));
    }

    public registerOnChange(fn: any): void {
        this.onChange = fn;
    }

    public registerOnTouched(fn: any): void {
        this.onTouched = fn;
    }

    public setDisabledState(isDisabled: boolean): void {
        this.disabled = isDisabled;
    }

    public onPatternStrategySelected(strategy: TemplateRef<any>) {
        this.parts.controls['strategy'].setValue(TranslationStringPatternInputComponent.STRATEGY_ORDER[this.strategyTemplates.indexOf(strategy)]);
    }

    private updateDisableState() {
        this.disabled ? this.parts.disable() : this.parts.enable();
        this.stateChanges.next();
    }
}
