import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ConfirmWorkspaceDeletionComponent } from './confirm-workspace-deletion.component';

describe('ConfirmDeletionComponent', () => {
  let component: ConfirmWorkspaceDeletionComponent;
  let fixture: ComponentFixture<ConfirmWorkspaceDeletionComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ConfirmWorkspaceDeletionComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ConfirmWorkspaceDeletionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
