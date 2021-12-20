import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsWorkspaceTreeNodeComponent } from './repository-details-workspace-tree-node.component';

describe('RepositoryDetailsWorkspaceTreeNodeComponent', () => {
  let component: RepositoryDetailsWorkspaceTreeNodeComponent;
  let fixture: ComponentFixture<RepositoryDetailsWorkspaceTreeNodeComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsWorkspaceTreeNodeComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsWorkspaceTreeNodeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
