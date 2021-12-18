import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSaveButtonComponent } from './form-save-button.component';

describe('FormSaveButtonComponent', () => {
  let component: FormSaveButtonComponent;
  let fixture: ComponentFixture<FormSaveButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormSaveButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSaveButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
