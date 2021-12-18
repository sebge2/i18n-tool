import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSearchButtonComponent } from './form-search-button.component';

describe('FormSearchButtonComponent', () => {
  let component: FormSearchButtonComponent;
  let fixture: ComponentFixture<FormSearchButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [FormSearchButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSearchButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
