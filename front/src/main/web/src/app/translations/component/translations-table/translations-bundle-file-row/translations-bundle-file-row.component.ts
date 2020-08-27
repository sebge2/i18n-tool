import {Component, Input, OnDestroy, OnInit} from '@angular/core';
import {WorkspaceService} from "../../../service/workspace.service";
import {Subject} from "rxjs";
import {takeUntil} from "rxjs/operators";
import {BundleFile, BundleType} from "../../../model/workspace/bundle-file.model";
import * as _ from "lodash";

@Component({
    selector: 'app-translations-bundle-file-row',
    templateUrl: './translations-bundle-file-row.component.html',
    styleUrls: ['./translations-bundle-file-row.component.css']
})
export class TranslationsBundleFileRowComponent implements OnInit, OnDestroy {

    @Input() public workspaceId: string;
    @Input() public bundleFileId: string;

    public bundleFile: BundleFile;

    private _destroyed$ = new Subject<void>();

    constructor(private _workspaceService: WorkspaceService) {
    }

    public ngOnInit(): void {
        this._workspaceService
            .getWorkspaceBundleFile(this.workspaceId)
            .pipe(takeUntil(this._destroyed$))
            .subscribe((bundleFiles: BundleFile[]) =>
                this.bundleFile = _.find(bundleFiles, bundleFile => _.isEqual(bundleFile.id, this.bundleFileId))
            );
    }

    public ngOnDestroy(): void {
        this._destroyed$.next();
        this._destroyed$.complete();
    }

    public get fileTypeClass(): string {
        if(!this.bundleFile){
            return "";
        }

        switch (this.bundleFile.type) {
            case BundleType.JAVA_PROPERTIES:
                return 'app-icon-java-file';
            case BundleType.JSON_ICU:
                return 'app-icon-json-file';
            default:
                return '';
        }
    }

    public get name(): string {
        if(!this.bundleFile){
            return "";
        }

        switch (this.bundleFile.type) {
            case BundleType.JSON_ICU:
                return `${this.bundleFile.location}`;
            case BundleType.JAVA_PROPERTIES:
            default:
                return `${this.bundleFile.location}/${this.bundleFile.name}`;
        }
    }
}
