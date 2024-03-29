import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryAddWizardStepInitializationComponent } from './repository-add-wizard-step-initialization.component';

describe('RepositoryAddWizardStepInitializationComponent', () => {
  let component: RepositoryAddWizardStepInitializationComponent;
  let fixture: ComponentFixture<RepositoryAddWizardStepInitializationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryAddWizardStepInitializationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardStepInitializationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
