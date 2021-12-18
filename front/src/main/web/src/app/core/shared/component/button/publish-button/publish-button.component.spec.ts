import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PublishButtonComponent } from './publish-button.component';

describe('PublishButtonComponent', () => {
  let component: PublishButtonComponent;
  let fixture: ComponentFixture<PublishButtonComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [PublishButtonComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PublishButtonComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
