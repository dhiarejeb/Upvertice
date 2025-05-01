import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProvidershipManagerComponent } from './providership-manager.component';

describe('ProvidershipManagerComponent', () => {
  let component: ProvidershipManagerComponent;
  let fixture: ComponentFixture<ProvidershipManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvidershipManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProvidershipManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
