import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GeneratePasswordButtonComponent } from './generate-password-button.component';

describe('GeneratePasswordButtonComponent', () => {
  let component: GeneratePasswordButtonComponent;
  let fixture: ComponentFixture<GeneratePasswordButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [GeneratePasswordButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GeneratePasswordButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
