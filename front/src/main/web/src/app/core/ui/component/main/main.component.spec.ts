import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { MainComponent } from './main.component';
import { CoreSharedModule } from '@i18n-core-shared';
import { RouterModule } from '@angular/router';
import { CoreEventModule } from '@i18n-core-event';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { AuthenticationService } from '@i18n-core-auth';
import { BehaviorSubject } from 'rxjs';
import { AuthenticatedUser } from '@i18n-core-auth';
import { HeaderComponent } from '../header/header.component';
import { MenuComponent } from '../menu/menu.component';
import { CoreAuthModule } from '@i18n-core-auth';

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let authenticationService: AuthenticationService;
  let currentUser: BehaviorSubject<AuthenticatedUser>;

  beforeEach(waitForAsync(() => {
    currentUser = new BehaviorSubject<AuthenticatedUser>(null);
    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);
    authenticationService.currentAuthenticatedUser = jasmine.createSpy().and.returnValue(currentUser);

    TestBed.configureTestingModule({
      imports: [BrowserAnimationsModule, CoreSharedModule, CoreAuthModule, CoreEventModule, RouterModule.forRoot([], { relativeLinkResolution: 'legacy' })],
      declarations: [MainComponent, HeaderComponent, MenuComponent],
      providers: [{ provide: AuthenticationService, useValue: authenticationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
