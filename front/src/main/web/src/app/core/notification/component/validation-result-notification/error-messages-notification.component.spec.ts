import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ErrorMessagesNotificationComponent } from './error-messages-notification.component';

describe('ErrorMessagesNotificationComponent', () => {
  let component: ErrorMessagesNotificationComponent;
  let fixture: ComponentFixture<ErrorMessagesNotificationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorMessagesNotificationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorMessagesNotificationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
