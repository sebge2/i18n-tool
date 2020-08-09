import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryDetailsWorkspacesComponent } from './repository-details-workspaces.component';

describe('RepositoryDetailsWorkspacesComponent', () => {
  let component: RepositoryDetailsWorkspacesComponent;
  let fixture: ComponentFixture<RepositoryDetailsWorkspacesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryDetailsWorkspacesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsWorkspacesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
