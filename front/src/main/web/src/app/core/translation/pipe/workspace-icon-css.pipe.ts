import { Pipe, PipeTransform } from '@angular/core';
import { Workspace } from '../model/workspace/workspace.model';
import { WorkspaceStatus } from '../model/workspace/workspace-status.model';

@Pipe({
  name: 'workspaceIconCss',
})
export class WorkspaceIconCssPipe implements PipeTransform {
  transform(workspace: Workspace): any {
    switch (workspace.status) {
      case WorkspaceStatus.IN_REVIEW:
        return 'icon-workspace-in-review';
      case WorkspaceStatus.INITIALIZED:
        return 'icon-workspace-initialized';
      case WorkspaceStatus.NOT_INITIALIZED:
        return 'icon-workspace-not-initialized';
      default:
        return '';
    }
  }
}
