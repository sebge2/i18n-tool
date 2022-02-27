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
import { Subject } from 'rxjs';
import { AuthenticatedUser } from '../model/authenticated-user.model';
import { AuthenticationService } from '../service/authentication.service';
import { takeUntil } from 'rxjs/operators';

@Directive({
  selector: '[hasRepositoryAccess]',
})
export class HasRepositoryAccessDirective implements OnInit, OnDestroy {
  private readonly _destroyed$ = new Subject<void>();

  private _repository: string;
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

        this._evaluate();
      });
  }

  ngOnDestroy(): void {
    this._destroyed$.complete();
  }

  @Input()
  set hasRepositoryAccess(repository: string) {
    this._repository = repository;

    this._evaluate();
  }

  @Input()
  set hasRepositoryAccessAdditionalCondition(additionalCondition: boolean) {
    this._additionalCondition = additionalCondition;
    this._evaluate();
  }

  private _evaluate() {
    if (
      this._currentUser != null &&
      this._currentUser.hasRepositoryAccess(this._repository) &&
      this._additionalCondition
    ) {
      if (this._embeddedViewRef == null) {
        this._embeddedViewRef = this._viewContainer.createEmbeddedView(this._templateRef);
      }
    } else {
      this._viewContainer.clear();
      this._embeddedViewRef = null;
    }
  }
}
