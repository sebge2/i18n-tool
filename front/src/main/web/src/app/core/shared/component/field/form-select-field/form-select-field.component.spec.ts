import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSelectFieldComponent } from './form-select-field.component';

describe('FormSelectFieldComponent', () => {
  let component: FormSelectFieldComponent<any>;
  let fixture: ComponentFixture<FormSelectFieldComponent<any>>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormSelectFieldComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSelectFieldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
