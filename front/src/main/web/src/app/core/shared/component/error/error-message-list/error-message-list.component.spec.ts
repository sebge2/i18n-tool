import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ErrorMessageListComponent } from './error-message-list.component';

describe('ErrorMessageListComponent', () => {
  let component: ErrorMessageListComponent;
  let fixture: ComponentFixture<ErrorMessageListComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorMessageListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorMessageListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
