import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { WorkspaceBundleFileSelectorComponent } from './workspace-bundle-file-selector.component';

describe('WorkspaceBundleFileSelectorComponent', () => {
  let component: WorkspaceBundleFileSelectorComponent;
  let fixture: ComponentFixture<WorkspaceBundleFileSelectorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [WorkspaceBundleFileSelectorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceBundleFileSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
