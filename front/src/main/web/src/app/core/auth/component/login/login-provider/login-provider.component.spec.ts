import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LoginProviderComponent } from './login-provider.component';
import { CoreSharedModule } from '@i18n-core-shared';

describe('LoginProviderComponent', () => {
  let component: LoginProviderComponent;
  let fixture: ComponentFixture<LoginProviderComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      imports: [CoreSharedModule],
      declarations: [LoginProviderComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginProviderComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    expect(component).toBeTruthy(); // TODO issue-125
  });
});
