import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { Error404Component } from './error404.component';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { MainMessageComponent } from '@i18n-core-shared';

describe('Error404Component', () => {
  let component: Error404Component;
  let fixture: ComponentFixture<Error404Component>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [Error404Component, ErrorMessageComponent, MainMessageComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(Error404Component);
    component = fixture.componentInstance;
  }));

  it('should create', () => {
    fixture.detectChanges();

    expect(component).toBeTruthy(); // TODO issue-125
  });
});
