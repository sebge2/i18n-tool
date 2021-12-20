import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsWorkspacesComponent } from './repository-details-workspaces.component';

describe('RepositoryDetailsWorkspacesComponent', () => {
  let component: RepositoryDetailsWorkspacesComponent;
  let fixture: ComponentFixture<RepositoryDetailsWorkspacesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsWorkspacesComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsWorkspacesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
