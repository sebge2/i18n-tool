import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryAddWizardStepInfoComponent } from './repository-add-wizard-step-info.component';

describe('RepositoryAddWizardStepInfoComponent', () => {
  let component: RepositoryAddWizardStepInfoComponent;
  let fixture: ComponentFixture<RepositoryAddWizardStepInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryAddWizardStepInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardStepInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
