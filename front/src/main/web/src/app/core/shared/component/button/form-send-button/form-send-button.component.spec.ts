import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormSendButtonComponent } from './form-send-button.component';

describe('FormSendButtonComponent', () => {
  let component: FormSendButtonComponent;
  let fixture: ComponentFixture<FormSendButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FormSendButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSendButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
