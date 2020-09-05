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
    TemplateRef
} from '@angular/core';
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {MatFormFieldControl} from "@angular/material/form-field";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {FocusMonitor} from "@angular/cdk/a11y";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import * as _ from "lodash";

@Component({
    selector: 'app-form-select-field',
    templateUrl: './form-select-field.component.html',
    styleUrls: ['./form-select-field.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => FormSelectFieldComponent),
            multi: true
        },
        {provide: MatFormFieldControl, useExisting: FormSelectFieldComponent},
    ],
})
export class FormSelectFieldComponent<E> implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<E[]> {

    @Input() public availableValues: E[] = [];
    @Input() public itemTemplate: TemplateRef<any>;

    @HostBinding('attr.aria-describedby') public describedBy = '';
    @HostBinding() public id = `app-form-select-field-${FormSelectFieldComponent.nextId++}`;

    public stateChanges = new Subject<void>();
    public focused = false;
    public controlType = 'form-select-field';
    public errorState = false;
    public ngControl: NgControl;

    public readonly parts: FormGroup;

    private static nextId = 0;

    private onChange = (_: any) => {
    };
    private onTouched = () => {
    };
    private _placeholder: string;
    private _disabled = false;
    private _required = false;
    private readonly _destroyed$ = new Subject();

    constructor(private injector: Injector,
                private focusMonitor: FocusMonitor,
                private formBuilder: FormBuilder,
                private elRef: ElementRef<HTMLElement>) {
        this.parts = formBuilder.group({
            values: [[]]
        });

        this.parts.valueChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe(() => {
                this.onChange(this.value);
                this.stateChanges.next()
            });
    }

    public ngOnInit() {
        this.ngControl = this.injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }
    }

    public ngAfterViewInit(): void {
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
        return this._disabled;
    }

    public set disabled(value: boolean) {
        this._disabled = coerceBooleanProperty(value);
        this._disabled ? this.parts.disable() : this.parts.enable();
        this.stateChanges.next();
    }

    @Input()
    public get required() {
        return this._required;
    }

    public set required(req) {
        this._required = coerceBooleanProperty(req);
        this.stateChanges.next();
    }

    public get value(): E[] {
        return this.parts.controls['values'].value;
    }

    public set value(value: E[]) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    public get empty() {
        return _.isEmpty(this.parts.controls['values']);
    }

    @HostBinding('class.floating')
    public get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    public setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    public onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'mat-selection-list') {
            (<HTMLElement>this.elRef.nativeElement.querySelector('mat-selection-list')).focus();
        }
    }

    public writeValue(values: E[] | null): void {
        this.parts.controls['values'].setValue(values);
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
}
