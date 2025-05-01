import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorshipManagerComponent } from './sponsorship-manager.component';

describe('SponsorshipManagerComponent', () => {
  let component: SponsorshipManagerComponent;
  let fixture: ComponentFixture<SponsorshipManagerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorshipManagerComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorshipManagerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
