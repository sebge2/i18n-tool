import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryAddWizardStepCreationComponent } from './repository-add-wizard-step-creation.component';

describe('RepositoryAddWizardStepCreationComponent', () => {
  let component: RepositoryAddWizardStepCreationComponent;
  let fixture: ComponentFixture<RepositoryAddWizardStepCreationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryAddWizardStepCreationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardStepCreationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
