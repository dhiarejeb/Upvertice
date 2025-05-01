import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProvidershipDetailsComponent } from './providership-details.component';

describe('ProvidershipDetailsComponent', () => {
  let component: ProvidershipDetailsComponent;
  let fixture: ComponentFixture<ProvidershipDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ProvidershipDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ProvidershipDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
