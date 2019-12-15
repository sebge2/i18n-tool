import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormOpenTabButtonComponent } from './form-open-tab-button.component';

describe('FormOpenTabButtonComponent', () => {
  let component: FormOpenTabButtonComponent;
  let fixture: ComponentFixture<FormOpenTabButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormOpenTabButtonComponent ]
    })
    .compileComponents();
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
