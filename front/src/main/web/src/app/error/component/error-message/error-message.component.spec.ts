import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorMessageComponent } from './error-message.component';
import { MainMessageComponent } from '@i18n-core-shared';

describe('ErrorMessageComponent', () => {
  let component: ErrorMessageComponent;
  let fixture: ComponentFixture<ErrorMessageComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ErrorMessageComponent, MainMessageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorMessageComponent);
    component = fixture.componentInstance;
  }));

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
