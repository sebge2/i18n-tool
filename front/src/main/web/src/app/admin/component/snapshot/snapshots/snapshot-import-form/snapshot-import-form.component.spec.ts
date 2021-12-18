import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SnapshotImportFormComponent } from './snapshot-import-form.component';

describe('SnapshotImportFormComponent', () => {
  let component: SnapshotImportFormComponent;
  let fixture: ComponentFixture<SnapshotImportFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SnapshotImportFormComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SnapshotImportFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
