import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormDownloadActionButtonComponent } from './form-download-action-button.component';

describe('FormDownloadActionButtonComponent', () => {
  let component: FormDownloadActionButtonComponent;
  let fixture: ComponentFixture<FormDownloadActionButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormDownloadActionButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormDownloadActionButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
