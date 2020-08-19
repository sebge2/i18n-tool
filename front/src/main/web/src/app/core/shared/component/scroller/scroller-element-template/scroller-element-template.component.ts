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
    ViewContainerRef
} from '@angular/core';

@Component({
    selector: 'app-scroller-element-template',
    templateUrl: './scroller-element-template.component.html',
    styleUrls: ['./scroller-element-template.component.css']
})
export class ScrollerElementTemplateComponent implements AfterViewInit, OnDestroy {

    @Input() public element: any;
    @Input() public elementComponent: Type<any>;
    @ViewChild('target', {read: ViewContainerRef}) public target: ViewContainerRef;

    private componentRef: ComponentRef<any>

    constructor(private resolver: ComponentFactoryResolver) {
    }

    public ngAfterViewInit() {
        let factory: ComponentFactory<any> = this.resolver.resolveComponentFactory(this.elementComponent);

        this.ngOnDestroy();

        this.componentRef = this.target.createComponent(factory);
        this.componentRef.instance.element = this.element;
        this.componentRef.changeDetectorRef.detectChanges();
    }

    public ngOnDestroy(): void {
        if (this.componentRef) {
            this.componentRef.changeDetectorRef.detach();
            this.componentRef.destroy();
            this.componentRef = null;
        }
    }
}
