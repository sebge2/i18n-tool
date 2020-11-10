import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryGithubAccessKeyDialogComponent } from './repository-github-access-key-dialog.component';

describe('RepositoryGithubAccessKeyDialogComponent', () => {
  let component: RepositoryGithubAccessKeyDialogComponent;
  let fixture: ComponentFixture<RepositoryGithubAccessKeyDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryGithubAccessKeyDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryGithubAccessKeyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
