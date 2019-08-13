import {Component, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {WorkspaceStatus} from "../../model/workspace-status.model";
import {WorkspaceService} from "../../service/workspace.service";
import {
    StartReviewDialogModel,
    TranslationsStartReviewComponent
} from "../translations-start-review/translations-start-review.component";
import {MatDialog} from "@angular/material";

@Component({
    selector: 'app-translations',
    templateUrl: './translations.component.html',
    styleUrls: ['./translations.component.css']
})
export class TranslationsComponent implements OnInit {

    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();

    private _expanded: boolean = false;

    constructor(private workspaceService: WorkspaceService,
                private dialog: MatDialog) {
    }

    ngOnInit() {
    }

    get expanded(): boolean {
        return this._expanded;
    }

    set expanded(value: boolean) {
        this._expanded = value;
    }

    isWorkspaceNotInitialized(): boolean {
        return (this.searchRequest != null) && (this.searchRequest.workspace != null) && (this.searchRequest.workspace.status == WorkspaceStatus.NOT_INITIALIZED);
    }

    isWorkspaceInitialized(): boolean {
        return (this.searchRequest != null) && (this.searchRequest.workspace != null) && (this.searchRequest.workspace.status == WorkspaceStatus.INITIALIZED);
    }

    isWorkspaceInReview(): boolean {
        return (this.searchRequest != null) && (this.searchRequest.workspace != null) && (this.searchRequest.workspace.status == WorkspaceStatus.IN_REVIEW);
    }

    onSearch(searchRequest: TranslationsSearchRequest) {
        this.searchRequest = new TranslationsSearchRequest(searchRequest);

        this._expanded = false;

        if (this.isWorkspaceNotInitialized()) {
            this.workspaceService.initialize(this.searchRequest.workspace);
        }
    }

    onRequestInitialized(searchRequest: TranslationsSearchRequest) {
        this.onSearch(searchRequest);
    }

    onRequestChange(searchRequest: TranslationsSearchRequest) {
        if (this.searchRequest.workspace && searchRequest.workspace && this.searchRequest.workspace.id == searchRequest.workspace.id) {
            const launchSearch = this.searchRequest.workspace.status == WorkspaceStatus.NOT_INITIALIZED && searchRequest.workspace.status != WorkspaceStatus.NOT_INITIALIZED;

            this.searchRequest.workspace = searchRequest.workspace;

            if (launchSearch) {
                this.onSearch(searchRequest);
            }
        }
    }

    openStartReviewDialog(): void {
        this.dialog
            .open(TranslationsStartReviewComponent, {
                width: '250px',
                data: <StartReviewDialogModel> {comment: ""}
            })
            .afterClosed()
            .subscribe((result: StartReviewDialogModel) => {
                if(result){
                    this.workspaceService.startReview(this.searchRequest.workspace, result.comment);
                }
            });
    }

}
