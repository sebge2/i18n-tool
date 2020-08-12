import {
    AfterViewInit,
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    Type,
    ViewContainerRef
} from '@angular/core';
import {TreeNode} from "../tree.component";

@Component({
    selector: 'app-tree-node-template',
    templateUrl: './tree-node-template.component.html',
    styleUrls: ['./tree-node-template.component.css']
})
export class TreeNodeTemplateComponent implements AfterViewInit {

    @Input() public node: TreeNode;
    @Input() public nodeComponent: Type<any>;

    constructor(private resolver: ComponentFactoryResolver,
                private _viewContainerRef: ViewContainerRef) {
    }

    ngAfterViewInit() {
        let factory: ComponentFactory<any> = this.resolver.resolveComponentFactory(this.nodeComponent);
        let component: ComponentRef<any> = this._viewContainerRef.createComponent(factory);
        component.instance.node = this.node;
    }
}
