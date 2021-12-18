import { Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { TranslationsSearchRequest } from '../../model/search/translations-search-request.model';
import { TranslationsSearchCriterion } from '../../model/search/translations-search-criterion.model';
import { TranslationLocaleService } from '@i18n-core-translation';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { combineLatest, Observable, Subject } from 'rxjs';
import { map, take, takeUntil } from 'rxjs/operators';
import { TranslationLocale } from '@i18n-core-translation';
import { TranslationLocaleIconPipe } from '@i18n-core-translation';
import * as _ from 'lodash';
import { WorkspaceService } from '@i18n-core-translation';
import { RepositoryIconPipe } from '@i18n-core-translation';
import { getDefaultWorkspaces, Workspace } from '@i18n-core-translation';
import { ActivatedRoute, Params, Router } from '@angular/router';
import {
  getRouteParamEnum,
  getRouteParamObject,
  getRouteParamsCollection,
  getRouteParamSimpleValue,
  updateRouteParams,
} from '@i18n-core-shared';
import { filterOutUnavailableElements, mapAll, mapToSingleton } from '@i18n-core-shared';
import { BundleFile } from '@i18n-core-translation';
import { TranslationKeyPattern } from '../../model/search/translation-key-pattern.model';
import { TranslationStringPatternStrategy } from '../../model/search/translation-string-pattern-strategy.enum';

@Component({
  selector: 'app-translations-search-bar',
  templateUrl: './translations-search-bar.component.html',
  styleUrls: ['./translations-search-bar.component.css'],
})
export class TranslationsSearchBarComponent implements OnInit, OnDestroy {
  @Output() expandedChange: EventEmitter<Boolean> = new EventEmitter();
  @Output() onSearchChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

  form: FormGroup;

  private readonly _searchForAllLocales: Observable<boolean>;
  private readonly _searchForAllWorkspaces: Observable<boolean>;
  private _expanded: boolean;
  private _destroyed$ = new Subject<void>();

  constructor(
    private _localeService: TranslationLocaleService,
    private _localeIconPipe: TranslationLocaleIconPipe,
    private _repositoryIconPipe: RepositoryIconPipe,
    private _workspaceService: WorkspaceService,
    private _formBuilder: FormBuilder,
    private _route: ActivatedRoute,
    private _router: Router
  ) {
    this.form = _formBuilder.group({
      workspaces: [[], Validators.required],
      bundleFile: [null],
      locales: [[], Validators.required],
      criterion: [null, Validators.required],
      keyPattern: [null],
    });

    this._searchForAllLocales = combineLatest([
      this.form.controls['locales'].valueChanges,
      this._localeService.getAvailableLocales(),
    ]).pipe(map(([_, availableLocales]) => availableLocales.length === this.locales.length));

    this._searchForAllWorkspaces = combineLatest([
      this.form.controls['workspaces'].valueChanges,
      this._workspaceService.getWorkspaces(),
    ]).pipe(map(([_, availableWorkspaces]) => availableWorkspaces.length === this.workspaces.length));
  }

  ngOnInit() {
    combineLatest([
      this._route.queryParams,
      this._localeService.getAvailableLocales(),
      this._localeService.getDefaultLocales(),
      this._workspaceService.getWorkspaces(),
    ])
      .pipe(takeUntil(this._destroyed$))
      .subscribe(([routeParams, availableLocales, defaultLocales, availableWorkspaces]) => {
        this._updateWorkspaces(availableWorkspaces, routeParams);
        this._updateBundleFile(routeParams);
        this._updateLocales(availableLocales, routeParams, defaultLocales);
        this._updateCriterion(routeParams);
        this._updateKeyPattern(routeParams);
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  get expanded(): boolean {
    return this._expanded;
  }

  @Input()
  set expanded(value: boolean) {
    this._expanded = value;
    this.expandedChange.emit(this.expanded);
  }

  get workspaces(): Workspace[] {
    return this.form.controls['workspaces'].value;
  }

  get workspacesAsString(): string {
    return !_.isEmpty(this.workspaces)
      ? this.workspaces
          .map(
            (workspace) =>
              `<span class="${this._repositoryIconPipe.transform(workspace.repositoryType)}"></span> <b>${
                workspace.repositoryName
              } ${workspace.branch}</b>`
          )
          .join(', ')
      : null;
  }

  isSearchForAllWorkspaces(): Observable<boolean> {
    return this._searchForAllWorkspaces;
  }

  get bundleFile(): BundleFile | undefined {
    return this.form.controls['bundleFile'].value;
  }

  get locales(): TranslationLocale[] {
    return this.form.controls['locales'].value;
  }

  get criterion(): TranslationsSearchCriterion {
    return this.form.controls['criterion'].value;
  }

  get localesAsString(): string {
    return !_.isEmpty(this.locales)
      ? this.locales
          .map((locale) => `<span class="${this._localeIconPipe.transform(locale)}"></span> ${locale.displayName}`)
          .join(', ')
      : null;
  }

  isSearchForAllLocales(): Observable<boolean> {
    return this._searchForAllLocales;
  }

  get keyPattern(): TranslationKeyPattern {
    const value: TranslationKeyPattern = this.form.controls['keyPattern'].value;

    if (!value || _.isEmpty(value.pattern)) {
      return null;
    }

    return value;
  }

  get searchRequest(): TranslationsSearchRequest {
    return new TranslationsSearchRequest(
      this.workspaces,
      this.bundleFile,
      this.locales,
      this.criterion,
      this.keyPattern
    );
  }

  onSearch() {
    const request = this.searchRequest;
    const queryParams = TranslationsSearchBarComponent._toQueryParams(request);

    updateRouteParams(queryParams, this._route, this._router).finally(() => this.onSearchChange.emit(request));
  }

  private _updateWorkspaces(availableWorkspaces: Workspace[], routeParams: Params) {
    if (_.some(this.workspaces)) {
      this.form.controls['workspaces'].setValue(
        filterOutUnavailableElements(availableWorkspaces, this.workspaces, 'id')
      );
    } else {
      this.form.controls['workspaces'].setValue(
        getRouteParamsCollection(
          'workspace',
          routeParams,
          availableWorkspaces,
          getDefaultWorkspaces(availableWorkspaces),
          'id'
        )
      );
    }
  }

  private _updateBundleFile(routeParams: Params) {
    if (_.isNil(this.bundleFile) && this.workspaces.length == 1) {
      this._workspaceService
        .getWorkspaceBundleFiles(this.workspaces[0].id)
        .pipe(take(1))
        .toPromise()
        .then((availableBundleFiles) => {
          if (_.isNil(this.bundleFile)) {
            // still not selected
            this.form.controls['bundleFile'].setValue(
              getRouteParamObject('bundleFile', routeParams, availableBundleFiles, null, 'id')
            );
          }
        });
    }
  }

  private _updateLocales(
    availableLocales: TranslationLocale[],
    routeParams: Params,
    defaultLocales: TranslationLocale[]
  ) {
    if (_.some(this.locales)) {
      this.form.controls['locales'].setValue(filterOutUnavailableElements(availableLocales, this.locales, 'id'));
    } else {
      this.form.controls['locales'].setValue(
        getRouteParamsCollection('locale', routeParams, availableLocales, defaultLocales, 'id')
      );
    }
  }

  private _updateCriterion(routeParams: Params) {
    if (_.isNil(this.criterion)) {
      this.form.controls['criterion'].setValue(
        getRouteParamEnum(
          'criterion',
          TranslationsSearchCriterion,
          TranslationsSearchCriterion.MISSING_TRANSLATIONS,
          routeParams
        )
      );
    }
  }

  private _updateKeyPattern(routeParams: Params) {
    if (_.isNil(this.keyPattern)) {
      const value = getRouteParamSimpleValue('keyPatternValue', routeParams, null);
      const strategy = getRouteParamEnum(
        'keyPatternStrategy',
        TranslationStringPatternStrategy,
        TranslationStringPatternStrategy.CONTAINS,
        routeParams
      );

      if (!_.isEmpty(value)) {
        this.form.controls['keyPattern'].setValue(new TranslationKeyPattern(strategy, value));
      }
    }
  }

  private static _toQueryParams(request: TranslationsSearchRequest): [string, string[]][] {
    const keyPatternValue = _.get(request.keyPattern, 'pattern', null);
    const keyPatternStrategy = _.get(request.keyPattern, 'strategy', null);

    const params: [string, string[]][] = [
      ['workspace', mapAll(request.workspaces, 'id')],
      ['bundleFile', mapToSingleton(request.bundleFile, 'id')],
      ['locale', mapAll(request.locales, 'id')],
      ['criterion', [_.toString(request.criterion)]],
    ];

    if (keyPatternValue) {
      params.push(['keyPatternValue', [keyPatternValue]]);
    }

    if (keyPatternStrategy) {
      params.push(['keyPatternStrategy', [keyPatternStrategy]]);
    }

    return params;
  }
}
