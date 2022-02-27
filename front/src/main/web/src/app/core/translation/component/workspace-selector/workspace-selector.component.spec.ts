import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { WorkspaceSelectorComponent } from './workspace-selector.component';

describe('WorkspaceSelectorComponent', () => {
  let component: WorkspaceSelectorComponent;
  let fixture: ComponentFixture<WorkspaceSelectorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [WorkspaceSelectorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
