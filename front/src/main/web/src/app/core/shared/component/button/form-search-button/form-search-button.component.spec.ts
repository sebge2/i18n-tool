import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormSearchButtonComponent } from './form-search-button.component';

describe('FormSearchButtonComponent', () => {
  let component: FormSearchButtonComponent;
  let fixture: ComponentFixture<FormSearchButtonComponent>;

  beforeEach(waitForAsync(() => {
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
