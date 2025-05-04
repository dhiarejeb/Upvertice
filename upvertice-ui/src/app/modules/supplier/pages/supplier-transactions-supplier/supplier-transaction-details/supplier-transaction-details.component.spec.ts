import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierTransactionDetailsComponent } from './supplier-transaction-details.component';

describe('SupplierTransactionDetailsComponent', () => {
  let component: SupplierTransactionDetailsComponent;
  let fixture: ComponentFixture<SupplierTransactionDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SupplierTransactionDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierTransactionDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
