import {
    AfterViewInit,
    Component,
    ComponentFactory,
    ComponentFactoryResolver,
    ComponentRef,
    Input,
    Type,
    ViewChild,
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
    @ViewChild('target', {read: ViewContainerRef}) public target: ViewContainerRef;

    constructor(private resolver: ComponentFactoryResolver) {
    }

    ngAfterViewInit() {
        let factory: ComponentFactory<any> = this.resolver.resolveComponentFactory(this.nodeComponent);
        let component: ComponentRef<any> = this.target.createComponent(factory);
        component.instance.node = this.node;
    }
}
