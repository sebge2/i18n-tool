import { Pipe, PipeTransform } from '@angular/core';
import { Workspace } from '../model/workspace/workspace.model';
import { WorkspaceStatus } from '../model/workspace/workspace-status.model';

@Pipe({
  name: 'workspaceIcon',
})
export class WorkspaceIconPipe implements PipeTransform {
  transform(workspace: Workspace): any {
    switch (workspace.status) {
      case WorkspaceStatus.IN_REVIEW:
        return 'lock';
      case WorkspaceStatus.INITIALIZED:
        return 'check_circle';
      case WorkspaceStatus.NOT_INITIALIZED:
        return 'warning';
    }
  }
}
