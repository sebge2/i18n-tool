import {Component, OnInit} from '@angular/core';
import {TranslationsSearchRequest} from "../../model/translations-search-request.model";
import {WorkspaceStatus} from "../../model/workspace-status.model";
import {WorkspaceService} from "../../service/workspace.service";

@Component({
    selector: 'app-translations',
    templateUrl: './translations.component.html',
    styleUrls: ['./translations.component.css']
})
export class TranslationsComponent implements OnInit {

    searchRequest: TranslationsSearchRequest = new TranslationsSearchRequest();
    private _expanded: boolean = false;

    constructor(private workspaceService: WorkspaceService) {
    }

    ngOnInit() {
    }

    get expanded(): boolean {
        return this._expanded;
    }

    set expanded(value: boolean) {
        this._expanded = value;
    }

    isRepositoryNotInitialized(): boolean {
        return (this.searchRequest != null) && (this.searchRequest.workspace == null);
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

    onSearchRequestChange(searchRequest: TranslationsSearchRequest) {
        this.searchRequest = searchRequest;

        this._expanded = false;

        if (this.isWorkspaceNotInitialized()) {
            this.workspaceService.initialize(this.searchRequest.workspace);
        }
    }
}
