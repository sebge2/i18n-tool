import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslationsWorkspaceRowComponent } from './translations-workspace-row.component';

describe('TranslationsWorkspaceRowComponent', () => {
  let component: TranslationsWorkspaceRowComponent;
  let fixture: ComponentFixture<TranslationsWorkspaceRowComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TranslationsWorkspaceRowComponent ]
    })
    .compileComponents();
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
