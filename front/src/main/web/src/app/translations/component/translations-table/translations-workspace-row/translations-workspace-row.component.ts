import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {WorkspaceService} from "../../../service/workspace.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {EnrichedWorkspace} from "../../../model/workspace/enriched-workspace.model";

@Component({
    selector: 'app-translations-workspace-row',
    templateUrl: './translations-workspace-row.component.html',
    styleUrls: ['./translations-workspace-row.component.css']
})
export class TranslationsWorkspaceRowComponent implements OnInit, OnDestroy {

    @Input() public workspaceId: string;

    public workspace: EnrichedWorkspace;

    private _destroyed$ = new Subject<void>();

    constructor(private _workspaceService: WorkspaceService) {
    }

    public ngOnInit(): void {
        this._workspaceService
            .getEnrichedWorkspace(this.workspaceId)
            .pipe(takeUntil(this._destroyed$))
            .subscribe((workspace: EnrichedWorkspace) => this.workspace = workspace);
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

}
