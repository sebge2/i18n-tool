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
import {TranslationsSearchCriterion} from "../../../model/search/translations-search-criterion.model";
import {ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl} from "@angular/forms";
import {MatFormFieldControl} from "@angular/material/form-field";
import {MatAutocomplete} from "@angular/material/autocomplete";
import {Subject} from "rxjs";
import {FocusMonitor} from "@angular/cdk/a11y";
import {coerceBooleanProperty} from "@angular/cdk/coercion";
import * as _ from "lodash";
import {WorkspaceService} from "../../../service/workspace.service";
import {RepositoryService} from "../../../service/repository.service";
import {takeUntil} from "rxjs/operators";

@Component({
    selector: 'app-translation-criterion-selector',
    templateUrl: './translation-criterion-selector.component.html',
    styleUrls: ['./translation-criterion-selector.component.css'],
    providers: [
        {
            provide: NG_VALUE_ACCESSOR,
            useExisting: forwardRef(() => TranslationCriterionSelectorComponent),
            multi: true
        },
        {provide: MatFormFieldControl, useExisting: TranslationCriterionSelectorComponent}
    ],
})
export class TranslationCriterionSelectorComponent implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<TranslationsSearchCriterion> {

    @ViewChild('auto', {static: false}) public matAutocomplete: MatAutocomplete;
    @HostBinding('attr.aria-describedby') public describedBy = '';
    @HostBinding() public id = `app-workspace-selector-${TranslationCriterionSelectorComponent.nextId++}`;

    public stateChanges = new Subject<void>();
    public focused = false;
    public controlType = 'app-translation-criterion-selector';
    public errorState = false;
    public ngControl: NgControl;

    public readonly parts: FormGroup;
    public readonly TranslationsSearchCriterion = TranslationsSearchCriterion;

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
                private elRef: ElementRef<HTMLElement>,
                private _workspaceService: WorkspaceService,
                private _repositoryService: RepositoryService) {
        this.parts = formBuilder.group({
            criterion: [null],
        });
    }

    public ngOnInit() {
        this.ngControl = this.injector.get(NgControl);

        if (this.ngControl != null) {
            this.ngControl.valueAccessor = this;
        }

        this.parts.controls['criterion']
            .valueChanges
            .pipe(takeUntil(this._destroyed$))
            .subscribe(_ => this.onChange(this.value))
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

    public get value(): TranslationsSearchCriterion {
        return this.parts.controls['criterion'].value;
    }

    public set value(value: TranslationsSearchCriterion) {
        this.writeValue(value);

        this.onChange(value);
        this.stateChanges.next();
    }

    public get empty() {
        return _.isEmpty(this.parts.controls['criterion']);
    }

    @HostBinding('class.floating')
    public get shouldLabelFloat() {
        return this.focused || !this.empty;
    }

    public setDescribedByIds(ids: string[]) {
        this.describedBy = ids.join(' ');
    }

    public onContainerClick(event: MouseEvent) {
        if ((event.target as Element).tagName.toLowerCase() != 'button') {
            this.elRef.nativeElement.querySelector('button').focus();
        }
    }

    public writeValue(value: TranslationsSearchCriterion | null): void {
        this.parts.controls['criterion'].setValue(value);
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
