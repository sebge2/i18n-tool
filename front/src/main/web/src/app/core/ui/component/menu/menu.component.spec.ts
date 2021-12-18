import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuComponent } from './menu.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { CoreAuthModule } from '@i18n-core-auth';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthenticationService } from '@i18n-core-auth';
import { AuthenticatedUser } from '@i18n-core-auth';
import { BehaviorSubject } from 'rxjs';
import { ALL_USER_ROLES, UserRole } from '@i18n-core-auth';

describe('MenuComponent', () => {
  let component: MenuComponent;
  let fixture: ComponentFixture<MenuComponent>;

  let user: BehaviorSubject<AuthenticatedUser> = new BehaviorSubject<AuthenticatedUser>(
    new AuthenticatedUser(ALL_USER_ROLES)
  );
  let authenticationService: AuthenticationService;

  beforeEach(async(() => {
    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
    authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(user);

    TestBed.configureTestingModule({
      imports: [CoreSharedModule, CoreAuthModule, RouterTestingModule],
      declarations: [MenuComponent],
      providers: [{ provide: AuthenticationService, useValue: authenticationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MenuComponent);
    component = fixture.componentInstance;
  }));

  xit('should have all rights', () => {
    user.next(new AuthenticatedUser(ALL_USER_ROLES));

    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#menuAdmin')).not.toBeNull();
  });

  xit('should have limited rights', () => {
    user.next(new AuthenticatedUser([UserRole.MEMBER_OF_ORGANIZATION]));

    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('#menuAdmin')).toBeNull();
  });
});
