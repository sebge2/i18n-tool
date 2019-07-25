import {Pipe, PipeTransform} from '@angular/core';
import {Workspace} from "../model/workspace.model";
import {WorkspaceStatus} from "../model/workspace-status.model";

@Pipe({
    name: 'workspaceIconCss'
})
export class WorkspaceIconCssPipe implements PipeTransform {

    transform(workspace: Workspace): any {
        switch (workspace.status) {
            case WorkspaceStatus.IN_REVIEW:
                return "icon-in-review";
            case WorkspaceStatus.INITIALIZED:
                return "icon-initialized";
            case WorkspaceStatus.NOT_INITIALIZED:
                return "icon-not-initialized";
            default:
                return "";
        }
    }

}
