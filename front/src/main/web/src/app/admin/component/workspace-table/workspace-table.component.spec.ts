import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceTableComponent } from './workspace-table.component';

describe('WorkspaceTableComponent', () => {
  let component: WorkspaceTableComponent;
  let fixture: ComponentFixture<WorkspaceTableComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkspaceTableComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
