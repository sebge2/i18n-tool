import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RepositoryInitializerComponent } from './repository-initializer.component';

describe('RepositoryInitializerComponent', () => {
  let component: RepositoryInitializerComponent;
  let fixture: ComponentFixture<RepositoryInitializerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RepositoryInitializerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RepositoryInitializerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
