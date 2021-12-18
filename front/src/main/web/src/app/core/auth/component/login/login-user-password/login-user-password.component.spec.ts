import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginUserPasswordComponent } from './login-user-password.component';
import { Router } from '@angular/router';
import { AuthenticationService } from '@i18n-core-auth';
import { NotificationService } from '@i18n-core-notification';
import { CoreSharedModule } from '@i18n-core-shared';

describe('LoginUserPasswordComponent', () => {
  let component: LoginUserPasswordComponent;
  let fixture: ComponentFixture<LoginUserPasswordComponent>;
  let authenticationService: AuthenticationService;
  let notificationService: NotificationService;
  let router: Router;

  beforeEach(async(() => {
    router = jasmine.createSpyObj('router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [CoreSharedModule],
      declarations: [LoginUserPasswordComponent],
      providers: [
        { provide: NotificationService, useValue: notificationService },
        { provide: AuthenticationService, useValue: authenticationService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginUserPasswordComponent);
    component = fixture.componentInstance;
  }));
  it('should create', () => {
    expect(component).toBeTruthy(); // TODO issue-125
  });
});
