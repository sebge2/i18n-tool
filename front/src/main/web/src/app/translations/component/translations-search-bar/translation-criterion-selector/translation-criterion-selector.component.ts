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
  ViewChild,
} from '@angular/core';
import { TranslationsSearchCriterion } from '../../../model/search/translations-search-criterion.model';
import { ControlValueAccessor, FormBuilder, FormGroup, NG_VALUE_ACCESSOR, NgControl } from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { MatAutocomplete } from '@angular/material/autocomplete';
import { Subject } from 'rxjs';
import { FocusMonitor } from '@angular/cdk/a11y';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import * as _ from 'lodash';
import { WorkspaceService } from '@i18n-core-translation';
import { RepositoryService } from '@i18n-core-translation';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-translation-criterion-selector',
  templateUrl: './translation-criterion-selector.component.html',
  styleUrls: ['./translation-criterion-selector.component.css'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => TranslationCriterionSelectorComponent),
      multi: true,
    },
    { provide: MatFormFieldControl, useExisting: TranslationCriterionSelectorComponent },
  ],
})
export class TranslationCriterionSelectorComponent
  implements
    OnInit,
    OnDestroy,
    AfterViewInit,
    DoCheck,
    ControlValueAccessor,
    MatFormFieldControl<TranslationsSearchCriterion>
{
  @ViewChild('auto', { static: false }) matAutocomplete: MatAutocomplete;
  @HostBinding('attr.aria-describedby') describedBy = '';
  @HostBinding() id = `app-workspace-selector-${TranslationCriterionSelectorComponent.nextId++}`;

  stateChanges = new Subject<void>();
  focused = false;
  controlType = 'app-translation-criterion-selector';
  errorState = false;
  ngControl: NgControl;

  readonly parts: FormGroup;
  readonly TranslationsSearchCriterion = TranslationsSearchCriterion;

  private static nextId = 0;

  private onChange = (_: any) => {};
  private onTouched = () => {};
  private _placeholder: string;
  private _disabled = false;
  private _required = false;
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private injector: Injector,
    private focusMonitor: FocusMonitor,
    private formBuilder: FormBuilder,
    private elRef: ElementRef<HTMLElement>,
    private _workspaceService: WorkspaceService,
    private _repositoryService: RepositoryService
  ) {
    this.parts = formBuilder.group({
      criterion: [null],
    });
  }

  ngOnInit() {
    this.ngControl = this.injector.get(NgControl);

    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }

    this.parts.controls['criterion'].valueChanges
      .pipe(takeUntil(this._destroyed$))
      .subscribe((_) => this.onChange(this.value));
  }

  ngAfterViewInit(): void {
    this.focusMonitor
      .monitor(this.elRef.nativeElement, true)
      .pipe(takeUntil(this._destroyed$))
      .subscribe((origin) => {
        this.focused = !!origin;
        this.stateChanges.next();
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(null);
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

  get value(): TranslationsSearchCriterion {
    return this.parts.controls['criterion'].value;
  }

  set value(value: TranslationsSearchCriterion) {
    this.writeValue(value);

    this.onChange(value);
    this.stateChanges.next();
  }

  get empty() {
    return _.isEmpty(this.parts.controls['criterion']);
  }

  @HostBinding('class.floating')
  get shouldLabelFloat() {
    return this.focused || !this.empty;
  }

  setDescribedByIds(ids: string[]) {
    this.describedBy = ids.join(' ');
  }

  onContainerClick(event: MouseEvent) {
    if ((event.target as Element).tagName.toLowerCase() != 'button') {
      this.elRef.nativeElement.querySelector('button').focus();
    }
  }

  writeValue(value: TranslationsSearchCriterion | null): void {
    this.parts.controls['criterion'].setValue(value);
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
}
