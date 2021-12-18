import { Component } from '@angular/core';
import { TreeNode } from '@i18n-core-shared';

@Component({
  selector: 'app-repository-details-workspace-tree-node',
  templateUrl: './repository-details-workspace-tree-node.component.html',
  styleUrls: ['./repository-details-workspace-tree-node.component.css'],
})
export class RepositoryDetailsWorkspaceTreeNodeComponent {
  public node: TreeNode;
  public loading: boolean = false;

  constructor() {}
}
