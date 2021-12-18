import {
  Directive,
  ElementRef,
  EmbeddedViewRef,
  Input,
  OnDestroy,
  OnInit,
  TemplateRef,
  ViewContainerRef,
} from '@angular/core';
import { UserRole } from '../model/user-role.model';
import { AuthenticationService } from '../service/authentication.service';
import { Subject } from 'rxjs';
import { AuthenticatedUser } from '../model/authenticated-user.model';
import { takeUntil } from 'rxjs/operators';

@Directive({
  selector: '[hasRole]',
})
export class HasRoleDirective implements OnInit, OnDestroy {
  private readonly _destroyed$ = new Subject();

  private _roles: UserRole[];
  private _additionalCondition: boolean = true;
  private _currentUser: AuthenticatedUser = null;
  private _embeddedViewRef: EmbeddedViewRef<any>;

  constructor(
    private _element: ElementRef,
    private _templateRef: TemplateRef<any>,
    private _viewContainer: ViewContainerRef,
    private _authenticationService: AuthenticationService
  ) {}

  ngOnInit(): void {
    this._authenticationService
      .currentAuthenticatedUser()
      .pipe(takeUntil(this._destroyed$))
      .subscribe((currentUser) => {
        this._currentUser = currentUser;

        this.evaluate();
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.complete();
  }

  @Input()
  set hasRole(roles: UserRole[]) {
    this._roles = roles;

    this.evaluate();
  }

  @Input()
  set hasRoleAdditionalCondition(additionalCondition: boolean) {
    this._additionalCondition = additionalCondition;
    this.evaluate();
  }

  private evaluate() {
    if (this._currentUser != null && this._currentUser.hasAllRoles(this._roles) && this._additionalCondition) {
      if (this._embeddedViewRef == null) {
        this._embeddedViewRef = this._viewContainer.createEmbeddedView(this._templateRef);
      }
    } else {
      this._viewContainer.clear();
      this._embeddedViewRef = null;
    }
  }
}
