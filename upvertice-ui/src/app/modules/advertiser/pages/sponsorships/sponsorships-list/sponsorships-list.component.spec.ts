import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SponsorshipsListComponent } from './sponsorships-list.component';

describe('SponsorshipsListComponent', () => {
  let component: SponsorshipsListComponent;
  let fixture: ComponentFixture<SponsorshipsListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SponsorshipsListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SponsorshipsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
