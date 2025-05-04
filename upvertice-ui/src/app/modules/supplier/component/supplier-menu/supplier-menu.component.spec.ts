import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SupplierMenuComponent } from './supplier-menu.component';

describe('SupplierMenuComponent', () => {
  let component: SupplierMenuComponent;
  let fixture: ComponentFixture<SupplierMenuComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SupplierMenuComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SupplierMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
