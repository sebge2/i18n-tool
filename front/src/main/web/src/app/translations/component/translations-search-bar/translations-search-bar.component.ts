import {Component, EventEmitter, Input, OnDestroy, OnInit, Output} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {TranslationsSearchCriterion} from "../../model/translations-search-criterion.model";
import {TranslationLocaleService} from "../../service/translation-locale.service";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {combineLatest, Observable, Subject} from "rxjs";
import {map, takeUntil} from "rxjs/operators";
import {TranslationLocale} from "../../model/translation-locale.model";
import {TranslationLocaleIconPipe} from "../../../core/shared/pipe/translation-locale-icon.pipe";
import * as _ from "lodash";
import {WorkspaceService} from "../../service/workspace.service";
import {EnrichedWorkspace} from "../../model/workspace/enriched-workspace.model";
import {RepositoryIconPipe} from "../../../core/shared/pipe/repository-icon.pipe";

@Component({
    selector: 'app-translations-search-bar',
    templateUrl: './translations-search-bar.component.html',
    styleUrls: ['./translations-search-bar.component.css'],
})
export class TranslationsSearchBarComponent implements OnInit, OnDestroy {

    @Output() public expandedChange: EventEmitter<Boolean> = new EventEmitter();

    @Output() public requestChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();
    @Output() public requestInitChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();
    @Output() public onSearchChange: EventEmitter<TranslationsSearchRequest> = new EventEmitter();

    public form: FormGroup;

    private readonly _searchForAllLocales: Observable<boolean>;
    private readonly _searchForAllWorkspaces: Observable<boolean>;
    private _expanded: boolean;
    private _destroyed$ = new Subject<void>();

    constructor(private _localeService: TranslationLocaleService,
                private _localeIconPipe: TranslationLocaleIconPipe,
                private _repositoryIconPipe: RepositoryIconPipe,
                private _workspaceService: WorkspaceService,
                private _formBuilder: FormBuilder) {
        this.form = _formBuilder.group({
            workspaces: [[], Validators.required],
            locales: [[], Validators.required],
            criterion: [TranslationsSearchCriterion.MISSING_TRANSLATIONS, Validators.required]
        });

        this._searchForAllLocales = combineLatest([this.form.controls['locales'].valueChanges, this._localeService.getAvailableLocales()])
            .pipe(map(([_, availableLocales]) => availableLocales.length === this.locales.length));

        this._searchForAllWorkspaces = combineLatest([this.form.controls['workspaces'].valueChanges, this._workspaceService.getEnrichedWorkspaces()])
            .pipe(map(([_, availableWorkspaces]) => availableWorkspaces.length === this.workspaces.length));
    }

    public ngOnInit() {
        this._localeService.getDefaultLocales()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(defaultLocales => this.form.controls['locales'].setValue(defaultLocales));

        this._workspaceService.getEnrichedWorkspaces()
            .pipe(takeUntil(this._destroyed$))
            .subscribe(availableWorkspaces => {
                if (this.workspaces.length > 0) {
                    this.form.controls['workspaces'].setValue(
                        this.workspaces
                            .map(workspace =>
                                _.some(availableWorkspaces, availableWorkspace => availableWorkspace.workspace.equals(workspace.workspace))
                            )
                    );
                } else {
                    this.form.controls['workspaces'].setValue(
                        availableWorkspaces
                            .filter(availableWorkspace => availableWorkspace.defaultWorkspace)
                    );
                }
            });
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get expanded(): boolean {
        return this._expanded;
    }

    @Input()
    public set expanded(value: boolean) {
        this._expanded = value;
        this.expandedChange.emit(this.expanded);
    }

    public get criterion(): TranslationsSearchCriterion {
        return this.form.controls['criterion'].value;
    }

    public get workspaces(): EnrichedWorkspace[] {
        return this.form.controls['workspaces'].value;
    }

    public get workspacesAsString(): string {
        return !_.isEmpty(this.workspaces)
            ? this.workspaces
                .map(workspace => `<span class="${this._repositoryIconPipe.transform(workspace.repository.type)}"></span> <b>${workspace.repository.name} ${workspace.workspace.branch}</b>`)
                .join(', ')
            : null;
    }

    public isSearchForAllWorkspaces(): Observable<boolean> {
        return this._searchForAllWorkspaces;
    }

    public get locales(): TranslationLocale[] {
        return this.form.controls['locales'].value;
    }

    public get localesAsString(): string {
        return !_.isEmpty(this.locales)
            ? this.locales
                .map(locale => `<span class="${this._localeIconPipe.transform(locale)}"></span> ${locale.displayName}`)
                .join(', ')
            : null;
    }

    public isSearchForAllLocales(): Observable<boolean> {
        return this._searchForAllLocales;
    }

    public onSearch() {
        const searchRequest = new TranslationsSearchRequest();
        searchRequest.criterion = this.criterion;
        searchRequest.workspaces = this.workspaces;
        searchRequest.locales = this.locales;

        this.onSearchChange.emit(searchRequest);
    }
}
