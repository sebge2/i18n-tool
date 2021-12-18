import { Component, Input, OnInit, Type } from '@angular/core';
import { FlatTreeControl, TreeControl } from '@angular/cdk/tree';
import { CollectionViewer, DataSource, SelectionChange } from '@angular/cdk/collections';
import { BehaviorSubject, combineLatest, merge, Observable, of, Subject } from 'rxjs';
import { map, takeUntil, takeWhile } from 'rxjs/operators';
import * as _ from 'lodash';

export interface TreeObject {
  expandable: boolean;
}

export interface TreeObjectDataSource {
  getRootObjects(): Observable<TreeObject[]>;

  getChildren(parent: TreeObject, level: number): Observable<TreeObject[]>;
}

export class EmptyTreeObjectDataSource implements TreeObjectDataSource {
  public getRootObjects(): Observable<TreeObject[]> {
    return of([]);
  }

  public getChildren(parent: TreeObject, level: number): Observable<TreeObject[]> {
    return of([]);
  }
}

export class TreeNode {
  private _expanded$ = new BehaviorSubject<boolean>(false);

  constructor(public data: TreeObject, public level: number, public isLoading = false) {}

  public get expandable(): boolean {
    return this.data.expandable;
  }

  public get expanded(): Observable<boolean> {
    return this._expanded$;
  }

  public expand(expanded: boolean) {
    this._expanded$.next(expanded);
  }
}

export class TreeDataSource implements DataSource<TreeNode> {
  private _treeNodes$ = new BehaviorSubject<TreeNode[]>([]);
  private _destroyed$ = new Subject<void>();

  constructor(private _treeControl: TreeControl<any>, private _source: TreeObjectDataSource) {}

  connect(collectionViewer: CollectionViewer): Observable<TreeNode[] | ReadonlyArray<TreeNode>> {
    this._treeControl.expansionModel.changed.pipe(takeUntil(this._destroyed$)).subscribe((change) => {
      if ((change as SelectionChange<TreeNode>).added || (change as SelectionChange<TreeNode>).removed) {
        this.handleTreeControl(change as SelectionChange<TreeNode>);
      }
    });

    return merge(collectionViewer.viewChange, this._treeNodes$).pipe(
      takeUntil(this._destroyed$),
      map(() => this.treeNodes)
    );
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this._destroyed$.next();
    this._destroyed$.complete();
  }

  get treeNodes(): TreeNode[] {
    return this._treeNodes$.value;
  }

  set treeNodes(treeNodes: TreeNode[]) {
    this._treeControl.dataNodes = treeNodes;
    this._treeNodes$.next(treeNodes);
  }

  public load(): void {
    this._source
      .getRootObjects()
      .pipe(takeUntil(this._destroyed$))
      .subscribe(
        (rootNodes: TreeObject[]) => (this.treeNodes = rootNodes.map((rootNode) => new TreeNode(rootNode, 1)))
      );
  }

  private handleTreeControl(change: SelectionChange<TreeNode>) {
    if (change.added) {
      change.added.forEach((node) => this.toggleNode(node, true));
    }

    if (change.removed) {
      change.removed
        .slice()
        .reverse()
        .forEach((node) => this.toggleNode(node, false));
    }
  }

  private toggleNode(node: TreeNode, expand: boolean) {
    node.expand(expand);

    this.removeChildObjectsAtIndex(this.treeNodes.indexOf(node), node);

    if (expand) {
      node.isLoading = true;

      combineLatest([node.expanded, this._source.getChildren(node.data, node.level + 1)])
        .pipe(takeWhile(([expanded, childObjects]) => expanded))
        .subscribe(([expanded, childObjects]) => {
          node.isLoading = false;

          const index = this.treeNodes.indexOf(node);
          if (!childObjects || index < 0) {
            return;
          }

          this.removeChildObjectsAtIndex(this.treeNodes.indexOf(node), node);

          if (expand) {
            this.insertChildObjectsAtIndex(childObjects, node.level + 1, index);
          }
        });
    }
  }

  private insertChildObjectsAtIndex(childObjects: TreeObject[], level: number, index: number) {
    const updatedTreeNodes = _.clone(this.treeNodes);

    const childNodes = childObjects.map((childNode) => new TreeNode(childNode, level));

    updatedTreeNodes.splice(index + 1, 0, ...childNodes);

    this.treeNodes = updatedTreeNodes;
  }

  private removeChildObjectsAtIndex(parentIndex: number, parentNode: TreeNode) {
    const updatedTreeNodes = _.clone(this.treeNodes);

    let count = TreeDataSource.countNumberOfChildren(updatedTreeNodes, parentIndex, parentNode);
    updatedTreeNodes.splice(parentIndex + 1, count);

    this.treeNodes = updatedTreeNodes;
  }

  private static countNumberOfChildren(allTreeNodes: TreeNode[], parentIndex: number, parentNode: TreeNode) {
    let count = 0;
    for (let i = parentIndex + 1; i < allTreeNodes.length && allTreeNodes[i].level > parentNode.level; i++, count++) {}

    return count;
  }
}

@Component({
  selector: 'app-tree',
  templateUrl: './tree.component.html',
  styleUrls: ['./tree.component.css'],
})
export class TreeComponent implements OnInit {
  @Input() public nodeComponent: Type<any>;

  public treeControl: FlatTreeControl<TreeNode>;
  public dataSource: TreeDataSource;

  private _treeNodeDataSource: TreeObjectDataSource = new EmptyTreeObjectDataSource();

  constructor() {
    this.treeControl = new FlatTreeControl<TreeNode>(this.getLevel, this.isExpandable);
    this.dataSource = new TreeDataSource(this.treeControl, this._treeNodeDataSource);
  }

  @Input()
  public get treeNodeDataSource(): TreeObjectDataSource {
    return this._treeNodeDataSource;
  }

  public set treeNodeDataSource(treeNodeDataSource: TreeObjectDataSource) {
    this._treeNodeDataSource = treeNodeDataSource;
    this.dataSource = new TreeDataSource(this.treeControl, this._treeNodeDataSource);
  }

  public ngOnInit(): void {
    this.dataSource.load();
  }

  public getLevel = (node: TreeNode) => node.level;

  public isExpandable = (node: TreeNode) => node.expandable;

  public hasChild = (_: number, node: TreeNode) => node.expandable;
}
