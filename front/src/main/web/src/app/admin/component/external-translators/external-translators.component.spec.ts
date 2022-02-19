import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExternalTranslatorsComponent } from './external-translators.component';

describe('ExternalTranslatorsComponent', () => {
  let component: ExternalTranslatorsComponent;
  let fixture: ComponentFixture<ExternalTranslatorsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ExternalTranslatorsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ExternalTranslatorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
