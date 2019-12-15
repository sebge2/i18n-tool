import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { InitDownloadButtonComponent } from './init-download-button.component';

describe('InitDownloadButtonComponent', () => {
  let component: InitDownloadButtonComponent;
  let fixture: ComponentFixture<InitDownloadButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ InitDownloadButtonComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(InitDownloadButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
