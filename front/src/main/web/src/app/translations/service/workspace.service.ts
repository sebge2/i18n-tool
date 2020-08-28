import {Injectable} from '@angular/core';
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace/workspace.model";
import {combineLatest, Observable} from "rxjs";
import {Events} from 'src/app/core/event/model/events.model';
import {catchError, distinctUntilChanged, filter, map, mergeMap} from "rxjs/operators";
import {NotificationService} from "../../core/notification/service/notification.service";
import {synchronizedCollection} from "../../core/shared/utils/synchronized-observable-utils";
import {WorkspaceService as ApiWorkspaceService} from "../../api";
import * as _ from "lodash";
import {BundleFile} from "../model/workspace/bundle-file.model";
import {RepositoryService} from "./repository.service";
import {EnrichedWorkspace} from "../model/workspace/enriched-workspace.model";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService {

    private _workspaces$: Observable<Workspace[]>;
    private _enrichedWorkspaces$: Observable<EnrichedWorkspace[]>;

    constructor(private apiWorkspaceService: ApiWorkspaceService,
                private repositoryService: RepositoryService,
                private eventService: EventService,
                private notificationService: NotificationService) {
    }

    public getWorkspaces(): Observable<Workspace[]> {
        if (!this._workspaces$) {
            this._workspaces$ = synchronizedCollection(
                this.apiWorkspaceService.findAll4(),
                this.eventService.subscribeDto(Events.ADDED_WORKSPACE),
                this.eventService.subscribeDto(Events.UPDATED_WORKSPACE),
                this.eventService.subscribeDto(Events.DELETED_WORKSPACE),
                dto => Workspace.fromDto(dto),
                ((first, second) => first.id === second.id)
            )
                .pipe(catchError((reason) => {
                    console.error("Error while retrieving workspaces.", reason);
                    this.notificationService.displayErrorMessage("Error while retrieving workspaces.");
                    return [];
                }));
        }

        return this._workspaces$;
    }

    public getEnrichedWorkspaces(): Observable<EnrichedWorkspace[]> {
        if (!this._enrichedWorkspaces$) {
            this._enrichedWorkspaces$ = combineLatest([this.getWorkspaces(), this.repositoryService.getRepositories()])
                .pipe(map(([workspaces, repositories]) => {
                    return workspaces
                        .map(workspace =>
                            new EnrichedWorkspace(
                                _.find(repositories, repository => _.isEqual(repository.id, workspace.repositoryId)),
                                workspace
                            )
                        )
                        .filter(enrichedWorkspace => enrichedWorkspace.repository)
                }));
        }

        return this._enrichedWorkspaces$;
    }

    public getRepositoryWorkspaces(repositoryId: string): Observable<Workspace[]> {
        return this.getWorkspaces()
            .pipe(map(workspaces => workspaces.filter(workspace => _.isEqual(workspace.repositoryId, repositoryId))));
    }

    public getWorkspace(workspaceId: string): Observable<Workspace> {
        return this.getWorkspaces()
            .pipe(
                map(workspaces => _.find(workspaces, workspace => _.isEqual(workspace.id, workspaceId))),
                filter(workspace => !!workspace),
                distinctUntilChanged()
            );
    }

    public getEnrichedWorkspace(workspaceId: string): Observable<EnrichedWorkspace> {
        return this.getEnrichedWorkspaces()
            .pipe(
                map(workspaces => _.find(workspaces, workspace => _.isEqual(workspace.workspace.id, workspaceId))),
                filter(workspace => !!workspace),
                distinctUntilChanged()
            );
    }

    public getWorkspaceBundleFile(workspaceId: string): Observable<BundleFile[]> {
        return this.getWorkspace(workspaceId)
            .pipe(
                mergeMap(_ => this.apiWorkspaceService.findWorkspaceBundleFiles(workspaceId)),
                map(bundleFiles => bundleFiles.map(bundleFileDto => BundleFile.fromDto(bundleFileDto)))
            );
    }

    public initialize(workspaceId: string): Observable<Workspace> {
        return this.apiWorkspaceService
            .executeWorkspaceAction(workspaceId, 'INITIALIZE')
            .pipe(map(workspace => Workspace.fromDto(workspace)));
    }

    public delete(workspaceId: string): Observable<any> {
        return this.apiWorkspaceService
            .deleteWorkspace(workspaceId);
    }
}

export function workspaceSorter(first: Workspace, second: Workspace): number {
    if (first.id === second.id) {
        return 0;
    } else if ("master" === first.branch) {
        return -1;
    } else if ("master" === second.branch) {
        return 1;
    } else {
        return (first.branch < second.branch) ? -1 : 1;
    }
}
