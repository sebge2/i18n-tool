import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryGitCredentialsDialogComponent } from './repository-git-credentials-dialog.component';

describe('RepositoryGitCredentialsDialogComponent', () => {
  let component: RepositoryGitCredentialsDialogComponent;
  let fixture: ComponentFixture<RepositoryGitCredentialsDialogComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryGitCredentialsDialogComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryGitCredentialsDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
