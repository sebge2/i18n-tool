import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormSyncButtonComponent } from './form-sync-button.component';

describe('FormSyncButtonComponent', () => {
  let component: FormSyncButtonComponent;
  let fixture: ComponentFixture<FormSyncButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormSyncButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormSyncButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
