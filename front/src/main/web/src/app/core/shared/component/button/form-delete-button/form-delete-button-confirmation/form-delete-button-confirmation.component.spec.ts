import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormDeleteButtonConfirmationComponent } from './form-delete-button-confirmation.component';

describe('FormDeleteButtonConfirmationComponent', () => {
  let component: FormDeleteButtonConfirmationComponent;
  let fixture: ComponentFixture<FormDeleteButtonConfirmationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FormDeleteButtonConfirmationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormDeleteButtonConfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
