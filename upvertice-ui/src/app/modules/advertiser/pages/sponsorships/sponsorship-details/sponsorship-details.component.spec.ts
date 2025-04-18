import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorshipDetailsComponent } from './sponsorship-details.component';

describe('SponsorshipDetailsComponent', () => {
  let component: SponsorshipDetailsComponent;
  let fixture: ComponentFixture<SponsorshipDetailsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorshipDetailsComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorshipDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
