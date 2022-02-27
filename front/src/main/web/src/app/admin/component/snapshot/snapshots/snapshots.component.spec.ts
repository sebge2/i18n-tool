import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { SnapshotsComponent } from './snapshots.component';

describe('SnapshotsComponent', () => {
  let component: SnapshotsComponent;
  let fixture: ComponentFixture<SnapshotsComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [SnapshotsComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SnapshotsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
