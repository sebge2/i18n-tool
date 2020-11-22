import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormAddButtonComponent } from './form-add-button.component';

describe('FormAddButtonComponent', () => {
  let component: FormAddButtonComponent;
  let fixture: ComponentFixture<FormAddButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormAddButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormAddButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
