import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DownloadButtonComponent } from './download-button.component';

describe('InitDownloadButtonComponent', () => {
  let component: DownloadButtonComponent;
  let fixture: ComponentFixture<DownloadButtonComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [DownloadButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DownloadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
