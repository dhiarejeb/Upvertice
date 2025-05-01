import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorOffersComponent } from './sponsor-offers.component';

describe('SponsorOffersComponent', () => {
  let component: SponsorOffersComponent;
  let fixture: ComponentFixture<SponsorOffersComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorOffersComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorOffersComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
