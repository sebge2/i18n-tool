import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { FormOpenTabButtonComponent } from './form-open-tab-button.component';

describe('FormOpenTabButtonComponent', () => {
  let component: FormOpenTabButtonComponent;
  let fixture: ComponentFixture<FormOpenTabButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [FormOpenTabButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormOpenTabButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
