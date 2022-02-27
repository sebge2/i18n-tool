import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryAddWizardComponent } from './repository-add-wizard.component';

describe('RepositoryAddWizardComponent', () => {
  let component: RepositoryAddWizardComponent;
  let fixture: ComponentFixture<RepositoryAddWizardComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryAddWizardComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
