import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryAddWizardStepTypeComponent } from './repository-add-wizard-step-type.component';

describe('RepositoryAddWizardStepTypeComponent', () => {
  let component: RepositoryAddWizardStepTypeComponent;
  let fixture: ComponentFixture<RepositoryAddWizardStepTypeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryAddWizardStepTypeComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardStepTypeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
