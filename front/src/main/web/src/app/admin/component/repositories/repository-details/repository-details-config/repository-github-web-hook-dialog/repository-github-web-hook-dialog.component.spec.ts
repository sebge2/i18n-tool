import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryGithubWebHookDialogComponent } from './repository-github-web-hook-dialog.component';

describe('RepositoryGithubWebHookDialogComponent', () => {
  let component: RepositoryGithubWebHookDialogComponent;
  let fixture: ComponentFixture<RepositoryGithubWebHookDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryGithubWebHookDialogComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryGithubWebHookDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
