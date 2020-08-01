import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryAddWizardComponent } from './repository-add.component';

describe('RepositoryAddComponent', () => {
  let component: RepositoryAddWizardComponent;
  let fixture: ComponentFixture<RepositoryAddWizardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryAddWizardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryAddWizardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
