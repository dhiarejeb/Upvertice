import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorAdsComponent } from './sponsor-ads.component';

describe('SponsorAdsComponent', () => {
  let component: SponsorAdsComponent;
  let fixture: ComponentFixture<SponsorAdsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorAdsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorAdsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
