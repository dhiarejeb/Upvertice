import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierSupplierOffersComponent } from './supplier-supplier-offers.component';

describe('SupplierSupplierOffersComponent', () => {
  let component: SupplierSupplierOffersComponent;
  let fixture: ComponentFixture<SupplierSupplierOffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SupplierSupplierOffersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierSupplierOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
