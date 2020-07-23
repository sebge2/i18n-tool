import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { WorkspaceSelectorComponent } from './workspace-selector.component';

describe('WorkspaceSelectorComponent', () => {
  let component: WorkspaceSelectorComponent;
  let fixture: ComponentFixture<WorkspaceSelectorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ WorkspaceSelectorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(WorkspaceSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
