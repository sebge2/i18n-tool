import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { RepositoryDetailsTranslationsGlobalConfigurationComponent } from './repository-details-translations-global-configuration.component';

describe('RepositoryDetailsTranslationsGlobalConfigurationComponent', () => {
  let component: RepositoryDetailsTranslationsGlobalConfigurationComponent;
  let fixture: ComponentFixture<RepositoryDetailsTranslationsGlobalConfigurationComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [RepositoryDetailsTranslationsGlobalConfigurationComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryDetailsTranslationsGlobalConfigurationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  xit('should create', () => {
    expect(component).toBeTruthy();
  });
});
