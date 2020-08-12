import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsWorkspaceTreeNodeComponent } from './repository-details-workspace-tree-node.component';

describe('RepositoryDetailsWorkspaceTreeNodeComponent', () => {
  let component: RepositoryDetailsWorkspaceTreeNodeComponent;
  let fixture: ComponentFixture<RepositoryDetailsWorkspaceTreeNodeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsWorkspaceTreeNodeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsWorkspaceTreeNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
