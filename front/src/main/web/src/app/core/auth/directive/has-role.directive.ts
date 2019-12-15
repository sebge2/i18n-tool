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
import {UserRole} from "../model/user-role.model";
import {AuthenticationService} from "../service/authentication.service";
import {Subject} from "rxjs";
import {AuthenticatedUser} from "../model/authenticated-user.model";
import {takeUntil} from "rxjs/operators";

@Directive({
    selector: '[hasRole]'
})
export class HasRoleDirective implements OnInit, OnDestroy {

    private destroy$ = new Subject();

    private roles: UserRole[];
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
    set hasRole(roles: UserRole[]) {
        this.roles = roles;

        this.evaluate();
    }

    @Input()
    set hasRoleAdditionalCondition(additionalCondition: boolean) {
        this.additionalCondition = additionalCondition;
        this.evaluate();
    }

    private evaluate() {
        if ((this.currentUser != null) && this.currentUser.hasAllRoles(this.roles) && this.additionalCondition) {
            if (this.embeddedViewRef == null) {
                this.embeddedViewRef = this.viewContainer.createEmbeddedView(this.templateRef);
            }
        } else {
            this.viewContainer.clear();
            this.embeddedViewRef = null;
        }
    }

}
