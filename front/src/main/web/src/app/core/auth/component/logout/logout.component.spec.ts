import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LogoutComponent } from './logout.component';
import { AuthenticationService } from '../../service/authentication.service';
import { CoreSharedModule } from '@i18n-core-shared';

describe('LogoutComponent', () => {
  let component: LogoutComponent;
  let fixture: ComponentFixture<LogoutComponent>;
  let authenticationService: AuthenticationService;

  beforeEach(waitForAsync(() => {
    authenticationService = jasmine.createSpyObj('authenticationUser', ['currentUser']);

    TestBed.configureTestingModule({
      imports: [CoreSharedModule],
      declarations: [LogoutComponent],
      providers: [{ provide: AuthenticationService, useValue: authenticationService }],
    }).compileComponents();

    fixture = TestBed.createComponent(LogoutComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    expect(component).toBeTruthy(); // TODO issue-125
  });
});
