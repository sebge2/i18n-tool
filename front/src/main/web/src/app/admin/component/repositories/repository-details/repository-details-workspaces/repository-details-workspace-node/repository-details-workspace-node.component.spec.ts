import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsWorkspaceNodeComponent } from './repository-details-workspace-node.component';

describe('RepositoryDetailsWorkspaceNodeComponent', () => {
  let component: RepositoryDetailsWorkspaceNodeComponent;
  let fixture: ComponentFixture<RepositoryDetailsWorkspaceNodeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsWorkspaceNodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsWorkspaceNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
