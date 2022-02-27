import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { TranslationsWorkspaceRowComponent } from './translations-workspace-row.component';

describe('TranslationsWorkspaceRowComponent', () => {
  let component: TranslationsWorkspaceRowComponent;
  let fixture: ComponentFixture<TranslationsWorkspaceRowComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [TranslationsWorkspaceRowComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TranslationsWorkspaceRowComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
