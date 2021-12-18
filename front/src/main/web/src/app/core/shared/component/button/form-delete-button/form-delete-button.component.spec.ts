import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormDeleteButtonComponent } from './form-delete-button.component';

describe('FormDeleteButtonComponent', () => {
  let component: FormDeleteButtonComponent;
  let fixture: ComponentFixture<FormDeleteButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormDeleteButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormDeleteButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
