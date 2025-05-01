import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorshipTableComponent } from './sponsorship-table.component';

describe('SponsorshipTableComponent', () => {
  let component: SponsorshipTableComponent;
  let fixture: ComponentFixture<SponsorshipTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorshipTableComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorshipTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
