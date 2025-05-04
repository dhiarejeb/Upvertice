import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WelcomeSupplierComponent } from './welcome-supplier.component';

describe('WelcomeSupplierComponent', () => {
  let component: WelcomeSupplierComponent;
  let fixture: ComponentFixture<WelcomeSupplierComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WelcomeSupplierComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WelcomeSupplierComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
