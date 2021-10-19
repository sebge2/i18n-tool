import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormUploadButtonComponent } from './form-upload-button.component';

describe('FormUploadButtonComponent', () => {
  let component: FormUploadButtonComponent;
  let fixture: ComponentFixture<FormUploadButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormUploadButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormUploadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
