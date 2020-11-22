import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SnapshotCreationFormComponent } from './snapshot-creation-form.component';

describe('SnapshotCreationFormComponent', () => {
  let component: SnapshotCreationFormComponent;
  let fixture: ComponentFixture<SnapshotCreationFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ SnapshotCreationFormComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SnapshotCreationFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
