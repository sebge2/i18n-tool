import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryViewCardComponent } from './repository-view-card.component';

describe('RepositoryViewCardComponent', () => {
  let component: RepositoryViewCardComponent;
  let fixture: ComponentFixture<RepositoryViewCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryViewCardComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryViewCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
