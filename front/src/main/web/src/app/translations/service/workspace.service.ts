import {Injectable} from '@angular/core';
import {EventService} from "../../core/event/service/event.service";
import {Workspace} from "../model/workspace/workspace.model";
import {Observable, of} from "rxjs";
import {Events} from 'src/app/core/event/model/events.model';
import {catchError, distinctUntilChanged, filter, map, mergeMap, tap} from "rxjs/operators";
import {NotificationService} from "../../core/notification/service/notification.service";
import {WorkspaceDto, WorkspaceService as ApiWorkspaceService} from "../../api";
import * as _ from "lodash";
import {BundleFile} from "../model/workspace/bundle-file.model";
import {RepositoryService} from "./repository.service";
import {SynchronizedCollection} from "../../core/shared/utils/synchronized-collection";

@Injectable({
    providedIn: 'root'
})
export class WorkspaceService {

    private static readonly MAX_CACHED_BUNDLE_FILES = 100;

    private readonly _synchronizedWorkspaces$: SynchronizedCollection<WorkspaceDto, Workspace>;
    private readonly _workspaces$: Observable<Workspace[]>;

    private _cachedWorkspaceBundleFiles = new Map<string, BundleFile[]>();

    constructor(private apiWorkspaceService: ApiWorkspaceService,
                private repositoryService: RepositoryService,
                private eventService: EventService,
                private notificationService: NotificationService) {

        this._synchronizedWorkspaces$ = new SynchronizedCollection<WorkspaceDto, Workspace>(
            () => this.apiWorkspaceService.findAll4(),
            this.eventService.subscribeDto(Events.ADDED_WORKSPACE),
            this.eventService.subscribeDto(Events.UPDATED_WORKSPACE),
            this.eventService.subscribeDto(Events.DELETED_WORKSPACE),
            this.eventService.reconnected(),
            dto => Workspace.fromDto(dto),
            ((first, second) => first.id === second.id)
        );

        this._workspaces$ = this._synchronizedWorkspaces$
            .collection
            .pipe(catchError((reason) => {
                console.error('Error while retrieving workspaces.', reason);
                this.notificationService.displayErrorMessage('ADMIN.WORKSPACES.ERROR.GET_ALL');
                return [];
            }));

        this.getWorkspaces()
            .subscribe(() => this._cachedWorkspaceBundleFiles.clear());
    }

    public getWorkspaces(): Observable<Workspace[]> {
        return this._workspaces$;
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

    public getWorkspaceBundleFiles(workspaceId: string): Observable<BundleFile[]> {
        if (this._cachedWorkspaceBundleFiles.has(workspaceId)) {
            return of(this._cachedWorkspaceBundleFiles.get(workspaceId));
        }

        return this.getWorkspace(workspaceId)
            .pipe(
                mergeMap(_ => this.apiWorkspaceService.findWorkspaceBundleFiles(workspaceId)),
                map(bundleFiles => bundleFiles.map(bundleFileDto => BundleFile.fromDto(bundleFileDto))),
                tap(bundleFiles => this.cacheBundleFiles(workspaceId, bundleFiles))
            );
    }

    public getWorkspaceBundleFile(workspaceId: string, bundleFiledId: string): Observable<BundleFile> {
        return this.getWorkspaceBundleFiles(workspaceId)
            .pipe(map(bundleFiles => bundleFiles.find(bundleFile => _.eq(bundleFile.id, bundleFiledId))));
    }

    public initialize(workspaceId: string): Observable<Workspace> {
        return this.apiWorkspaceService
            .executeAction(workspaceId, '', 'INITIALIZE')
            .pipe(
                map(workspace => Workspace.fromDto(workspace)),
                tap(workspace => this._synchronizedWorkspaces$.update(workspace))
            );
    }

    public synchronize(repositoryId: string): Observable<Workspace[]> {
        return this.apiWorkspaceService
            .synchronize(repositoryId, 'SYNCHRONIZE')
            .pipe(
                map(workspaces => workspaces.map(workspace => Workspace.fromDto(workspace))),
                tap(workspaces => workspaces.forEach(workspace => this._synchronizedWorkspaces$.update(workspace)))
            );
    }

    public delete(workspace: Workspace): Observable<any> {
        return this.apiWorkspaceService
            .deleteWorkspace(workspace.id)
            .pipe(tap(workspace => this._synchronizedWorkspaces$.delete(workspace)));
    }

    public publishAll(workspaces: string[], comment: string): Observable<Workspace[]>{
        return this.apiWorkspaceService
            .publish({workspaces: workspaces, message: comment})
            .pipe(
                map(workspaces => _.map(workspaces, workspace => Workspace.fromDto(workspace))),
            );
    }

    private cacheBundleFiles(workspaceId: string, bundleFiles: BundleFile[]) {
        let numberCachedElements = 0;
        this._cachedWorkspaceBundleFiles.forEach((cachedWorkspace, cachedFiles) => numberCachedElements += cachedFiles.length);

        if (numberCachedElements >= WorkspaceService.MAX_CACHED_BUNDLE_FILES) {
            this._cachedWorkspaceBundleFiles.clear();
        }

        this._cachedWorkspaceBundleFiles.set(workspaceId, bundleFiles);
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
