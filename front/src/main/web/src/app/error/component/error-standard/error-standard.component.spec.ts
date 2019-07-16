import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorStandardComponent } from './error-standard.component';

describe('ErrorStandardComponent', () => {
  let component: ErrorStandardComponent;
  let fixture: ComponentFixture<ErrorStandardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ErrorStandardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ErrorStandardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
