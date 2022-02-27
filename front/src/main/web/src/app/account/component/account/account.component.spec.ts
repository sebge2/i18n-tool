import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AccountComponent } from './account.component';

describe('AccountComponent', () => {
  let component: AccountComponent;
  let fixture: ComponentFixture<AccountComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [AccountComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(AccountComponent);
    component = fixture.componentInstance;
  }));

  xit('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
