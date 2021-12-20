import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryGithubAccessKeyDialogComponent } from './repository-github-access-key-dialog.component';

describe('RepositoryGithubAccessKeyDialogComponent', () => {
  let component: RepositoryGithubAccessKeyDialogComponent;
  let fixture: ComponentFixture<RepositoryGithubAccessKeyDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryGithubAccessKeyDialogComponent],
    }).compileComponents();
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
