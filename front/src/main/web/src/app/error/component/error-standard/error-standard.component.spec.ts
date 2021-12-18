import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ErrorStandardComponent } from './error-standard.component';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { MainMessageComponent } from '@i18n-core-shared';
import { ActivatedRoute, Params } from '@angular/router';
import { Subject } from 'rxjs';

describe('ErrorStandardComponent', () => {
  let component: ErrorStandardComponent;
  let fixture: ComponentFixture<ErrorStandardComponent>;
  let route: ActivatedRoute;

  beforeEach(async(() => {
    route = new ActivatedRoute();
    route.params = new Subject<Params>();

    TestBed.configureTestingModule({
      declarations: [ErrorStandardComponent, ErrorMessageComponent, MainMessageComponent],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorStandardComponent);
    component = fixture.componentInstance;
  }));

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
