import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { HeaderComponent } from './header.component';
import { CoreEventModule } from '@i18n-core-event';
import { AuthenticationService } from '@i18n-core-auth';
import { BehaviorSubject } from 'rxjs';
import { Router } from '@angular/router';
import { AuthenticatedUser } from '@i18n-core-auth';
import { CoreSharedModule } from '@i18n-core-shared';

describe('HeaderComponent', () => {
  let component: HeaderComponent;
  let fixture: ComponentFixture<HeaderComponent>;
  let authenticationService: AuthenticationService;
  let currentUser: BehaviorSubject<AuthenticatedUser>;
  let router: Router;

  beforeEach(waitForAsync(() => {
    currentUser = new BehaviorSubject<AuthenticatedUser>(null);
    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
    authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(currentUser);
    router = jasmine.createSpyObj('router', ['navigate']);

    TestBed.configureTestingModule({
      imports: [CoreSharedModule, CoreEventModule],
      declarations: [HeaderComponent],
      providers: [
        { provide: AuthenticationService, useValue: authenticationService },
        { provide: Router, useValue: router },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(HeaderComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
