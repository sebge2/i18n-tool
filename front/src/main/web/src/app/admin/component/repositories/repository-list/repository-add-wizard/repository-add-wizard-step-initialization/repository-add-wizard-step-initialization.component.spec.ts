import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryAddWizardStepInitializationComponent } from './repository-add-wizard-step-initialization.component';

describe('RepositoryAddWizardStepInitializationComponent', () => {
  let component: RepositoryAddWizardStepInitializationComponent;
  let fixture: ComponentFixture<RepositoryAddWizardStepInitializationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryAddWizardStepInitializationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardStepInitializationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
