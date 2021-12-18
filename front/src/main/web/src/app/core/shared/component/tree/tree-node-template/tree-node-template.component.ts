import {
  AfterViewInit,
  Component,
  ComponentFactory,
  ComponentFactoryResolver,
  ComponentRef,
  Input,
  OnDestroy,
  Type,
  ViewChild,
  ViewContainerRef,
} from '@angular/core';
import { TreeNode } from '../tree.component';

@Component({
  selector: 'app-tree-node-template',
  template: '<ng-template #target></ng-template>',
})
export class TreeNodeTemplateComponent implements AfterViewInit, OnDestroy {
  @Input() public node: TreeNode;
  @Input() public nodeComponent: Type<any>;
  @ViewChild('target', { read: ViewContainerRef }) public target: ViewContainerRef;

  private componentRef: ComponentRef<any>;
  private _loading: boolean = false;

  constructor(private resolver: ComponentFactoryResolver) {}

  public ngAfterViewInit() {
    let factory: ComponentFactory<any> = this.resolver.resolveComponentFactory(this.nodeComponent);

    this.ngOnDestroy();

    this.componentRef = this.target.createComponent(factory);
    this.componentRef.instance.node = this.node;
    this.componentRef.changeDetectorRef.detectChanges();
  }

  public ngOnDestroy(): void {
    if (this.componentRef) {
      this.componentRef.changeDetectorRef.detach();
      this.componentRef.destroy();
      this.componentRef = null;
    }
  }

  @Input()
  public get loading(): boolean {
    return this._loading;
  }

  public set loading(value: boolean) {
    this._loading = value;

    if (this.componentRef) {
      this.componentRef.instance.loading = value;
    }
  }
}
