import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { GitHubLinkButtonComponent } from './git-hub-link-button.component';

describe('GithubLinkButtonComponent', () => {
  let component: GitHubLinkButtonComponent;
  let fixture: ComponentFixture<GitHubLinkButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [GitHubLinkButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GitHubLinkButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
