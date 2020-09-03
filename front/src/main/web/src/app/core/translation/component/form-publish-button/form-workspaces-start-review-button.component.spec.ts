import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { FormWorkspacesStartReviewButtonComponent } from './form-workspaces-start-review-button.component';

describe('FormPublishButtonComponent', () => {
  let component: FormWorkspacesStartReviewButtonComponent;
  let fixture: ComponentFixture<FormWorkspacesStartReviewButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ FormWorkspacesStartReviewButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(FormWorkspacesStartReviewButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
