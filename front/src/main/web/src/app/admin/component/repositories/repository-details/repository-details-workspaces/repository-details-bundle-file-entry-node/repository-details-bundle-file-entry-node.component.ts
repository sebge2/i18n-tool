import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {WorkspaceBundleFileEntryTreeNode} from "../repository-details-workspaces.component";
import {TranslationLocaleService} from "../../../../../../translations/service/translation-locale.service";
import {map, takeUntil} from "rxjs/operators";
import {TranslationLocale} from "../../../../../../translations/model/translation-locale.model";
import {BehaviorSubject, combineLatest, Observable, Subject} from "rxjs";
import * as _ from "lodash";
import {RepositoryType} from "../../../../../../translations/model/repository/repository-type.model";
import {
    GitHubFileLink,
    GitHubLink
} from "../../../../../../core/shared/component/button/git-hub-link-button/git-hub-link-button.component";
import {GitHubRepository} from "../../../../../../translations/model/repository/github-repository.model";

@Component({
    selector: 'app-repository-details-bundle-file-entry-node',
    templateUrl: './repository-details-bundle-file-entry-node.component.html',
    styleUrls: ['./repository-details-bundle-file-entry-node.component.css']
})
export class RepositoryDetailsBundleFileEntryNodeComponent implements OnInit, OnDestroy {

    public locale$: Observable<TranslationLocale>;

    private _node: WorkspaceBundleFileEntryTreeNode;
    private readonly _localeId$ = new BehaviorSubject<string>(null);
    private readonly _destroyed$ = new Subject<void>();

    constructor(private translationLocaleService: TranslationLocaleService) {
    }

    public ngOnInit(): void {
        this.locale$ = combineLatest([this._localeId$, this.translationLocaleService.getAvailableLocales()])
            .pipe(
                takeUntil(this._destroyed$),
                map(([localeId, availableLocales]) => _.find(availableLocales, availableLocale => _.isEqual(availableLocale.id, localeId)))
            );
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    @Input()
    public get node(): WorkspaceBundleFileEntryTreeNode {
        return this._node;
    }

    public set node(value: WorkspaceBundleFileEntryTreeNode) {
        this._node = value;
        this._localeId$.next(this._node.bundleFileEntry.locale);
    }

    public get name(): string {
        return this.node.bundleFileEntry.file;
    }

    public get bundleLink(): GitHubFileLink {
        if(this.node && this.node.repository.type === RepositoryType.GITHUB){
            const repository = <GitHubRepository>this.node.repository;

            return new GitHubFileLink(
                repository.username,
                repository.repository,
                this.node.workspace.branch,
                this.node.bundleFileEntry.file
            );
        } else {
            return null;
        }
    }

}
