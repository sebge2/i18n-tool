import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LocaleAddCardComponent } from './locale-add-card.component';

describe('LocaleAddCardComponent', () => {
  let component: LocaleAddCardComponent;
  let fixture: ComponentFixture<LocaleAddCardComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LocaleAddCardComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LocaleAddCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
