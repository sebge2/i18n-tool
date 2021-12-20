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
import {
  ControlValueAccessor,
  FormBuilder,
  FormGroup,
  NG_VALUE_ACCESSOR,
  NgControl,
  SelectControlValueAccessor,
} from '@angular/forms';
import { MatFormFieldControl } from '@angular/material/form-field';
import { BundleFile } from '../../model/workspace/bundle-file.model';
import { BehaviorSubject, Observable, of, Subject } from 'rxjs';
import { FocusMonitor } from '@angular/cdk/a11y';
import { WorkspaceService } from '../../service/workspace.service';
import { map, mergeMap, takeUntil, tap } from 'rxjs/operators';
import { coerceBooleanProperty } from '@angular/cdk/coercion';
import * as _ from 'lodash';
import { Workspace } from '../../model/workspace/workspace.model';
import { NotificationService } from '@i18n-core-notification';

@Component({
  selector: 'app-workspace-bundle-file-selector',
  templateUrl: './workspace-bundle-file-selector.component.html',
  styleUrls: ['./workspace-bundle-file-selector.component.scss'],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => WorkspaceBundleFileSelectorComponent),
      multi: true,
    },
    { provide: MatFormFieldControl, useExisting: WorkspaceBundleFileSelectorComponent },
  ],
})
// https://itnext.io/creating-a-custom-form-field-control-compatible-with-reactive-forms-and-angular-material-cf195905b451
// https://material.angular.io/guide/creating-a-custom-form-field-control
export class WorkspaceBundleFileSelectorComponent
  implements OnInit, OnDestroy, AfterViewInit, DoCheck, ControlValueAccessor, MatFormFieldControl<BundleFile>
{
  @Input() public labelKey: string = '';

  @ViewChild('selectControl') public selectControl: SelectControlValueAccessor;
  @HostBinding('attr.aria-describedby') public describedBy = '';
  @HostBinding() public id = `app-translation-locale-selector-${WorkspaceBundleFileSelectorComponent.nextId++}`;

  public stateChanges = new Subject<void>();
  public focused = false;
  public controlType = 'workspace-bundle-file-selector';
  public errorState = false;
  public ngControl: NgControl;

  public readonly parts: FormGroup;
  public loading = false;
  public opened = false;

  public readonly bundleFiles: Observable<BundleFile[]>;
  public readonly bundleFilesEmpty: Observable<boolean>;

  private static nextId = 0;

  private onChange = (_: any) => {};
  private onTouched = () => {};
  private _placeholder: string;
  private _disabled = false;
  private _forceDisabled = false;
  private _required = false;

  private readonly _workspace$ = new BehaviorSubject<Workspace>(null);
  private readonly _destroyed$ = new Subject<void>();

  constructor(
    private injector: Injector,
    private focusMonitor: FocusMonitor,
    private formBuilder: FormBuilder,
    private elRef: ElementRef<HTMLElement>,
    private _workspaceService: WorkspaceService,
    private _notificationService: NotificationService
  ) {
    this.parts = formBuilder.group({
      bundleFile: [null],
    });

    this.bundleFiles = this._workspace$.pipe(
      takeUntil(this._destroyed$),
      mergeMap((workspace) => {
        if (workspace) {
          this.loading = true;

          return this._workspaceService.getWorkspaceBundleFiles(workspace.id).pipe(
            map((bundleFiles) => bundleFiles || []),
            tap(
              () => (this.loading = false),
              () => (this.loading = false)
            )
          );
        } else {
          return of(null);
        }
      })
    );

    this.bundleFilesEmpty = this.bundleFiles.pipe(map((bundleFiles) => !bundleFiles || !bundleFiles.length));

    this.parts.controls['bundleFile'].valueChanges
      .pipe(takeUntil(this._destroyed$))
      .subscribe((_) => this.onChange(this.value));
  }

  public ngOnInit() {
    this.ngControl = this.injector.get(NgControl);

    if (this.ngControl != null) {
      this.ngControl.valueAccessor = this;
    }

    if (!this.workspace) {
      this.forceDisabled = true;
    }
  }

  public ngAfterViewInit(): void {
    this.focusMonitor
      .monitor(this.elRef.nativeElement, true)
      .pipe(takeUntil(this._destroyed$))
      .subscribe((origin) => {
        this.focused = !!origin;
        this.stateChanges.next();
      });
  }

  public ngOnDestroy(): void {
    this._destroyed$.next(null);
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

  public get value(): BundleFile {
    return this.parts.controls['bundleFile'].value;
  }

  public set value(value: BundleFile) {
    this.writeValue(value);

    this.onChange(value);
    this.stateChanges.next();
  }

  public get empty() {
    return _.isEmpty(this.parts.controls['bundleFile'].value);
  }

  @HostBinding('class.floating')
  public get shouldLabelFloat() {
    return this.focused || !this.empty;
  }

  public setDescribedByIds(ids: string[]) {
    this.describedBy = ids.join(' ');
  }

  public onContainerClick(event: MouseEvent) {
    if ((event.target as Element).tagName.toLowerCase() != 'span') {
      this.elRef.nativeElement.querySelector('span').focus();
    }
  }

  public writeValue(value: BundleFile | null): void {
    this.parts.controls['bundleFile'].setValue(value);
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

  @Input()
  public get workspace(): Workspace {
    return this._workspace$.getValue();
  }

  public set workspace(workspace: Workspace) {
    if (!_.eq(_.get(workspace, 'id'), _.get(this.workspace, 'id'))) {
      this._workspace$.next(workspace);
      this.value = null;
      this.forceDisabled = !this.workspace;
    }
  }

  public onOpen(opened: boolean) {
    this.opened = opened;
  }

  private updateDisableState() {
    this.disabled ? this.parts.disable() : this.parts.enable();
    this.stateChanges.next();
  }
}
