import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { LocalesComponent } from './locales.component';

describe('LocalesComponent', () => {
  let component: LocalesComponent;
  let fixture: ComponentFixture<LocalesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [LocalesComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocalesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
