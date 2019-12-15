import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EditPreferencesComponent } from './edit-preferences.component';

describe('EditPreferencesComponent', () => {
  let component: EditPreferencesComponent;
  let fixture: ComponentFixture<EditPreferencesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EditPreferencesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EditPreferencesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
