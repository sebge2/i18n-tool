import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ScrollerElementTemplateComponent } from './scroller-element-template.component';

describe('ScrollerElementTemplateComponent', () => {
  let component: ScrollerElementTemplateComponent;
  let fixture: ComponentFixture<ScrollerElementTemplateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ScrollerElementTemplateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ScrollerElementTemplateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
