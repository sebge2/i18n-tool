import {
    Directive,
    ElementRef,
    EmbeddedViewRef,
    Input,
    OnDestroy,
    OnInit,
    TemplateRef,
    ViewContainerRef
} from '@angular/core';
import {Subject} from "rxjs";
import {AuthenticatedUser} from "../model/authenticated-user.model";
import {AuthenticationService} from "../service/authentication.service";
import {takeUntil} from "rxjs/operators";

@Directive({
    selector: '[hasRepositoryAccess]'
})
export class HasRepositoryAccessDirective implements OnInit, OnDestroy {

    private destroy$ = new Subject();

    private repository: string;
    private additionalCondition: boolean = true;
    private currentUser: AuthenticatedUser = null;
    private embeddedViewRef: EmbeddedViewRef<any>;

    constructor(private element: ElementRef,
                private templateRef: TemplateRef<any>,
                private viewContainer: ViewContainerRef,
                private authenticationService: AuthenticationService) {
    }

    ngOnInit(): void {
        this.authenticationService.currentAuthenticatedUser()
            .pipe(takeUntil(this.destroy$))
            .subscribe(currentUser => {
                this.currentUser = currentUser;

                this.evaluate();
            });
    }

    ngOnDestroy(): void {
        this.destroy$.complete();
    }

    @Input()
    public set hasRepositoryAccess(repository: string) {
        this.repository = repository;

        this.evaluate();
    }

    @Input()
    public set hasRepositoryAccessAdditionalCondition(additionalCondition: boolean) {
        this.additionalCondition = additionalCondition;
        this.evaluate();
    }

    private evaluate() {
        if ((this.currentUser != null) && this.currentUser.hasRepositoryAccess(this.repository) && this.additionalCondition) {
            if (this.embeddedViewRef == null) {
                this.embeddedViewRef = this.viewContainer.createEmbeddedView(this.templateRef);
            }
        } else {
            this.viewContainer.clear();
            this.embeddedViewRef = null;
        }
    }

}
