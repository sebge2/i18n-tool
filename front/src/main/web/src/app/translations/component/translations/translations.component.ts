import {Component, OnDestroy, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {WorkspaceStatus} from "../../model/workspace/workspace-status.model";
import {WorkspaceService} from "../../service/workspace.service";
import {
    StartReviewDialogModel,
    TranslationsStartReviewComponent
} from "../translations-start-review/translations-start-review.component";
import {MatDialog} from "@angular/material";
import {Workspace} from "../../model/workspace/workspace.model";
import {Subject} from "rxjs";

@Component({
    selector: 'app-translations',
    templateUrl: './translations.component.html',
    styleUrls: ['./translations.component.css']
})
export class TranslationsComponent implements OnInit, OnDestroy {

    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();
    readOnlyTable: boolean;
    startReviewing: boolean;
    startReviewAllowed = false;

    private destroy$ = new Subject();
    private _expanded: boolean = false;

    constructor(private workspaceService: WorkspaceService,
                private dialog: MatDialog) {
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    get expanded(): boolean {
        return this._expanded;
    }

    set expanded(value: boolean) {
        this._expanded = value;
    }

    onSearch(searchRequest: TranslationsSearchRequest) {
        this.searchRequest = new TranslationsSearchRequest(searchRequest);

        this._expanded = false;

        this.onWorkspaceUpdate(this.searchRequest.workspace);
    }

    onRequestInitialized(searchRequest: TranslationsSearchRequest) {
        this.onSearch(searchRequest);
    }

    onRequestChange(searchRequest: TranslationsSearchRequest) {
        if (this.searchRequest.workspace && searchRequest.workspace && this.searchRequest.workspace.id == searchRequest.workspace.id) {
            const launchSearch = this.searchRequest.workspace.isNotInitialized() && !searchRequest.workspace.isNotInitialized();

            this.searchRequest.workspace = searchRequest.workspace;

            if (launchSearch) {
                this.onSearch(searchRequest);
            } else {
                this.onWorkspaceUpdate(this.searchRequest.workspace);
            }
        }
    }

    openStartReviewDialog(): void {
        this.dialog
            .open(TranslationsStartReviewComponent, {
                width: '250px',
                data: <StartReviewDialogModel>{comment: ""}
            })
            .afterClosed()
            .subscribe((result: StartReviewDialogModel) => {
                if (result) {
                    this.startReviewing = true;

                    // this.workspaceService
                    //     .startReview(this.searchRequest.workspace, result.comment)
                    //     .finally(() => {
                    //         this.startReviewing = false;
                    //     });
                }
            });
    }

    private onWorkspaceUpdate(workspace: Workspace): void {
        switch (workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
                this.readOnlyTable = true;
                break;
            case WorkspaceStatus.NOT_INITIALIZED:
                this.readOnlyTable = true;
                // this.workspaceService.initialize(this.searchRequest.workspace);
                break;
            case  WorkspaceStatus.INITIALIZED:
                this.readOnlyTable = false;
                break;
            default :
                throw Error("Workspace status not supported [" + workspace.status + "]");
        }
    }

}
